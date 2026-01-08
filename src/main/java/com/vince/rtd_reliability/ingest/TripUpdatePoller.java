package com.vince.rtd_reliability.ingest;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime;
import com.vince.rtd_reliability.model.DelaySample;
import com.vince.rtd_reliability.repository.DelaySampleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class TripUpdatePoller {

    private static final String TRIP_UPDATES_URL =
            "https://open-data.rtd-denver.com/files/gtfs-rt/rtd/TripUpdate.pb";

    private final RestTemplate restTemplate = new RestTemplate();
    private final DelaySampleRepository delaySampleRepository;
    private final AirportToUnionTripIdCache tripIdCache;

    public TripUpdatePoller(
            DelaySampleRepository delaySampleRepository, AirportToUnionTripIdCache tripIdCache) {
        this.delaySampleRepository = delaySampleRepository;
        this.tripIdCache = tripIdCache;
    }

    @Scheduled(fixedDelay = 15_000)
    public void pollTripUpdates() throws InvalidProtocolBufferException {

        Set<String> desiredTripIds = tripIdCache.getCachedTripIds();
        List<DelaySample> samples = new ArrayList<>();

        byte[] bytes = fetchTripUpdatesBytes();
        if (bytes == null || bytes.length == 0) return;

        GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(bytes);
    }

    private byte[] fetchTripUpdatesBytes() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response =
                restTemplate.exchange(TRIP_UPDATES_URL, HttpMethod.GET, request, byte[].class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn(
                    "Trip Updates HTTP code {} - message {}",
                    response.getStatusCode(),
                    request.getBody());
        }

        return response.getBody();
    }
}
