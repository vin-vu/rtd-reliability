package com.vince.rtd_reliability.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GtfsScheduleService {

    private final Map<String, String> scheduledArrivalTimeCache = new ConcurrentHashMap<>();

    private final JdbcTemplate jdbcTemplate;

    public GtfsScheduleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> getScheduledArrivalTime(String tripId, String stopId) {

        String key = tripId + ":" + stopId;

        String sql =
                """
                SELECT arrival_time
                FROM gtfs_stop_times
                WHERE trip_id = ? AND stop_id = ?;
                """;

        String value =
                scheduledArrivalTimeCache.computeIfAbsent(
                        key,
                        k -> {
                            List<String> result =
                                    jdbcTemplate.queryForList(sql, String.class, tripId, stopId);

                            return result.isEmpty() ? null : result.get(0);
                        });

        return Optional.ofNullable(value);
    }
}
