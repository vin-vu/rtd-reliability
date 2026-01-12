package com.vince.rtd_reliability.repository;

import com.vince.rtd_reliability.view.OtpStatsView;
import com.vince.rtd_reliability.model.DelaySample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DelaySampleRepository extends JpaRepository<DelaySample, Long> {

    @Query(
            value =
                    """
        SELECT
          COUNT(*) AS total,
          COUNT(*) FILTER (WHERE delay_seconds < -300) AS early,
          COUNT(*) FILTER (WHERE delay_seconds BETWEEN -300 AND 300) AS on_time,
          COUNT(*) FILTER (WHERE delay_seconds > 300) AS late,
          ROUND(
            100.0 * COUNT(*) FILTER (WHERE delay_seconds BETWEEN -300 AND 300)
            / NULLIF(COUNT(*), 0),
            1
          ) AS on_time_pct
        FROM delay_samples
        WHERE route_id = :routeId
          AND stop_id IN (:stopId1, :stopId2)
        """,
            nativeQuery = true)
    OtpStatsView getOtpLifetime(
            @Param("routeId") String routeId,
            @Param("stopId1") String stopId1,
            @Param("stopId2") String stopId2);
}
