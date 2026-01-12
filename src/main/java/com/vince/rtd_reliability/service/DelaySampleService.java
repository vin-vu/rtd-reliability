package com.vince.rtd_reliability.service;

import com.vince.rtd_reliability.interfaces.OtpStatsView;
import com.vince.rtd_reliability.model.DelaySample;
import com.vince.rtd_reliability.model.OtpStats;
import com.vince.rtd_reliability.repository.DelaySampleRepository;
import com.vince.rtd_reliability.repository.DelaySampleUpsertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DelaySampleService {

    private final DelaySampleUpsertRepository upsertRepo;
    private final DelaySampleRepository repo;

    public DelaySampleService(DelaySampleUpsertRepository upsertRepo, DelaySampleRepository repo) {
        this.upsertRepo = upsertRepo;
        this.repo = repo;
    }

    @Transactional
    public void saveAll(List<DelaySample> samples) {
        if (samples.isEmpty()) return;

        for (DelaySample sample : samples) {
            upsertRepo.upsert(
                    sample.getRouteId(),
                    sample.getTripId(),
                    sample.getStopId(),
                    sample.getDelaySeconds(),
                    sample.getSampledAt());
        }
    }

    public OtpStats getOtpLifeTime(String routeId, String stopId1, String stopId2) {
        OtpStatsView view = repo.getOtpLifetime(routeId, stopId1, stopId2);
        return new OtpStats(
                view.getTotal(),
                view.getEarly(),
                view.getOnTime(),
                view.getLate(),
                view.getOnTimePct());
    }
}
