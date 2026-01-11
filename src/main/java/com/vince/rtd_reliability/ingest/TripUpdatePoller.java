package com.vince.rtd_reliability.ingest;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime;
import com.vince.rtd_reliability.model.DelaySample;
import com.vince.rtd_reliability.service.DelaySampleService;
import com.vince.rtd_reliability.service.GtfsScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class TripUpdatePoller {

    private static final String TRIP_UPDATES_URL =
            "https://open-data.rtd-denver.com/files/gtfs-rt/rtd/TripUpdate.pb";
    private static final ZoneId AGENCY_TZ = ZoneId.of("America/Denver");

    private final RestTemplate restTemplate = new RestTemplate();
    private final DelaySampleService delaySampleService;
    private final UnionTripIdCache tripIdCache;
    private final GtfsScheduleService gtfsScheduleService;

    public TripUpdatePoller(
            DelaySampleService delaySampleService,
            UnionTripIdCache tripIdCache,
            GtfsScheduleService gtfsScheduleService) {
        this.delaySampleService = delaySampleService;
        this.tripIdCache = tripIdCache;
        this.gtfsScheduleService = gtfsScheduleService;
    }

    @Scheduled(fixedDelay = 15_000)
    public void pollTripUpdates() throws InvalidProtocolBufferException {

        Set<String> desiredTripIds = tripIdCache.getCachedTripIds();
        Set<String> unionStopIds = tripIdCache.getCachedStopIds();
        List<DelaySample> samples = new ArrayList<>();

        byte[] bytes = fetchTripUpdatesBytes();
        if (bytes == null || bytes.length == 0) return;

        GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(bytes);

        for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
            if (!entity.hasTripUpdate()) continue;

            GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();

            if (!tripUpdate.hasTrip()) continue;
            String tripId = tripUpdate.getTrip().getTripId();
            if (!desiredTripIds.contains(tripId)) continue;

            List<GtfsRealtime.TripUpdate.StopTimeUpdate> filteredStuList =
                    filterStopTimeUpdates(tripUpdate, unionStopIds);

            for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : filteredStuList) {

                long rtArrivalTime = stu.getArrival().getTime();

                Optional<String> scheduledArrivalTimeOptional =
                        gtfsScheduleService.getScheduledArrivalTime(tripId, stu.getStopId());

                if (scheduledArrivalTimeOptional.isEmpty()) continue;
                String scheduledArrivalTime = scheduledArrivalTimeOptional.get();

                long scheduledArrivalTimeEpoch =
                        gtfsTimeToEpochSeconds(scheduledArrivalTime, rtArrivalTime);

                long arrivalTimeDelta = rtArrivalTime - scheduledArrivalTimeEpoch;
                Instant sampledAt = Instant.ofEpochSecond(feed.getHeader().getTimestamp());

                DelaySample sample =
                        new DelaySample(
                                tripUpdate.getTrip().getRouteId(),
                                tripId,
                                stu.getStopId(),
                                arrivalTimeDelta,
                                sampledAt);

                samples.add(sample);

                log.info(
                        "tripId: {} - stopId: {} - delta delay: {}",
                        tripId,
                        stu.getStopId(),
                        arrivalTimeDelta);
            }
        }
        if (!samples.isEmpty()) delaySampleService.saveAll(samples);
    }

    private byte[] fetchTripUpdatesBytes() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<byte[]> response =
                    restTemplate.exchange(TRIP_UPDATES_URL, HttpMethod.GET, request, byte[].class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn(
                        "Trip Updates request failed: status={} - ur={}",
                        response.getStatusCode(),
                        TRIP_UPDATES_URL);
            }

            return response.getBody();

        } catch (RestClientException e) {
            log.warn(
                    "Trip Updates request failed: url={} error={}",
                    TRIP_UPDATES_URL,
                    e.getMessage());
        }
        return null;
    }

    private List<GtfsRealtime.TripUpdate.StopTimeUpdate> filterStopTimeUpdates(
            GtfsRealtime.TripUpdate tripUpdate, Set<String> stopIds) {

        List<GtfsRealtime.TripUpdate.StopTimeUpdate> result = new ArrayList<>();

        for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()) {
            if (stu.getScheduleRelationship()
                    == GtfsRealtime.TripUpdate.StopTimeUpdate.ScheduleRelationship.SKIPPED)
                continue;

            if (!stu.hasArrival() || !stu.getArrival().hasTime()) continue;

            if (!stopIds.contains(stu.getStopId())) continue;

            result.add(stu);
        }
        return result;
    }

    private long gtfsTimeToEpochSeconds(String scheduledTime, long feedTimeStampEpochSec) {

        LocalDate serviceDate =
                Instant.ofEpochSecond(feedTimeStampEpochSec).atZone(AGENCY_TZ).toLocalDate();

        String[] parts = scheduledTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        int sec = Integer.parseInt(parts[2]);

        // handle GTFS times go beyond 24:00:00 since working in service days
        int dayOffset = hour / 24;
        int hourOfDay = hour % 24;

        LocalTime localTime = LocalTime.of(hourOfDay, minute, sec);
        LocalDate localDate = serviceDate.plusDays(dayOffset);

        return ZonedDateTime.of(localDate, localTime, AGENCY_TZ).toEpochSecond();
    }
}
