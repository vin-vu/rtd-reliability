package com.vince.rtd_reliability.ingest;

import com.vince.rtd_reliability.repository.DelaySampleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class TripUpdatePoller {

    private static final String TRIP_UPDATES_URL = "https://open-data.rtd-denver.com/files/gtfs-rt/rtd/TripUpdate.pb";

    private final RestTemplate restTemplate = new RestTemplate();
    private final DelaySampleRepository delaySampleRepository;

    public TripUpdatePoller(DelaySampleRepository delaySampleRepository) {
        this.delaySampleRepository = delaySampleRepository;
    }


}
