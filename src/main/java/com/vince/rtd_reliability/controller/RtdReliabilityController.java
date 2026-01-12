package com.vince.rtd_reliability.controller;

import com.vince.rtd_reliability.ingest.UnionGtfsCache;
import com.vince.rtd_reliability.model.OtpStats;
import com.vince.rtd_reliability.service.DelaySampleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RtdReliabilityController {

    @Value("${rtd.route-id}")
    private String routeId;

    private final DelaySampleService delaySampleService;
    private final UnionGtfsCache unionGtfsCache;

    public RtdReliabilityController(
            DelaySampleService delaySampleService, UnionGtfsCache unionGtfsCache) {
        this.delaySampleService = delaySampleService;
        this.unionGtfsCache = unionGtfsCache;
    }

    @GetMapping("/otp-lifetime")
    public ResponseEntity<?> getOtpLifeTime() {
        List<String> stopIds = new ArrayList<>(unionGtfsCache.getCachedStopIds());

        if (stopIds.size() < 2) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Stop ID error (expected 2)");
        }

        Collections.sort(stopIds);
        String stopId1 = stopIds.get(0);
        String stopId2 = stopIds.get(1);

        OtpStats stats = delaySampleService.getOtpLifeTime(routeId, stopId1, stopId2);
        return ResponseEntity.ok(stats);
    }
}
