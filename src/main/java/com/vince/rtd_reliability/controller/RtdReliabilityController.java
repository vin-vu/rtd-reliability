package com.vince.rtd_reliability.controller;

import com.vince.rtd_reliability.ingest.UnionTripIdCache;
import com.vince.rtd_reliability.model.OtpStats;
import com.vince.rtd_reliability.service.DelaySampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Set;

@RestController
public class RtdReliabilityController {

    private final DelaySampleService delaySampleService;
    private final UnionTripIdCache unionTripIdCache;

    public RtdReliabilityController(
            DelaySampleService delaySampleService, UnionTripIdCache unionTripIdCache) {
        this.delaySampleService = delaySampleService;
        this.unionTripIdCache = unionTripIdCache;
    }

    @GetMapping("/otp-lifetime")
    public OtpStats getOtpLifeTime() {
        Set<String> cachedStopIds = unionTripIdCache.getCachedStopIds();
        Iterator<String> iterator = cachedStopIds.iterator();
        String stopId1 = iterator.next();
        String stopId2 = iterator.next();

        String routeId = "15";

        return delaySampleService.getOtpLifeTime(routeId, stopId1, stopId2);
    }
}
