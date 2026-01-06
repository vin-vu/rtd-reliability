package com.vince.rtd_reliability.repository;

import com.vince.rtd_reliability.model.DelaySample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelaySampleRepository extends JpaRepository<DelaySample, Long> {}
