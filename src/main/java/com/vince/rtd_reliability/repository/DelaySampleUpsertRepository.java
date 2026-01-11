package com.vince.rtd_reliability.repository;

import com.vince.rtd_reliability.model.DelaySample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface DelaySampleUpsertRepository extends JpaRepository<DelaySample, Long> {

    @Modifying
    @Transactional
    @Query(
            value =
                    """
        INSERT INTO delay_samples (route_id, trip_id, stop_id, delay_seconds, sampled_At)
        VALUES (:routeId, :tripId, :stopId, :delaySeconds, :sampledAt)
        ON CONFLICT (trip_id, stop_id)
        DO UPDATE SET
          route_id = EXCLUDED.route_id,
          delay_seconds = EXCLUDED.delay_seconds,
          sampled_at = EXCLUDED.sampled_At
        """,
            nativeQuery = true)
    void upsert(
            @Param("routeId") String routeId,
            @Param("tripId") String tripId,
            @Param("stopId") String stopId,
            @Param("delaySeconds") int delaySeconds,
            @Param("sampledAt") Instant sampledAt);
}
