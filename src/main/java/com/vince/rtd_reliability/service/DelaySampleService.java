package com.vince.rtd_reliability.service;

import com.vince.rtd_reliability.model.DelaySample;
import com.vince.rtd_reliability.repository.DelaySampleUpsertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DelaySampleService {

    private final DelaySampleUpsertRepository repo;

    public DelaySampleService(DelaySampleUpsertRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void saveAll(List<DelaySample> samples) {
        if (samples.isEmpty()) return;

        for (DelaySample sample : samples) {
            repo.upsert(
                    sample.getRouteId(),
                    sample.getTripId(),
                    sample.getStopId(),
                    sample.getDelaySeconds(),
                    sample.getSampledAt()
                    );
        }
    }
}
