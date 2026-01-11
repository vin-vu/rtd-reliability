package com.vince.rtd_reliability.service;

import com.vince.rtd_reliability.model.DelaySample;
import com.vince.rtd_reliability.repository.DelaySampleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DelaySampleService {

    private final DelaySampleRepository repo;

    public DelaySampleService(DelaySampleRepository repo) {
        this.repo = repo;
    }

    public void saveAll(List<DelaySample> samples) {
        if (samples.isEmpty()) return;
        repo.saveAll(samples);
    }
}
