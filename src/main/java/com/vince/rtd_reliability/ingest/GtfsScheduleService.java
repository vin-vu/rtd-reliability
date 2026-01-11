package com.vince.rtd_reliability.ingest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GtfsScheduleService {

    private final JdbcTemplate jdbcTemplate;

    public GtfsScheduleService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> getScheduledArrivalTime(String tripId, String stopId) {
        String sql =
                """
                SELECT arrival_time
                FROM gtfs_stop_times
                WHERE trip_id = ? AND stop_id = ?;
                """;

        List<String> result = jdbcTemplate.queryForList(sql, String.class, tripId, stopId);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
