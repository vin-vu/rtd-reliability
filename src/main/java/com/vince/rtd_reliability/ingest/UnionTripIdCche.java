package com.vince.rtd_reliability.ingest;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class UnionTripIdCche {

    private static final String routeId = "15";
    private static final String stopName = "%union%";
    private static final String AIRPORT_STOP_ID = "34476";
    private static final String UNION_STOP_ID = "34667";
    private final JdbcTemplate jdbcTemplate;

    @Getter
    private volatile Set<String> cachedTripIds = Set.of();

    @Getter
    private volatile Set<String> cachedStopIds = Set.of();

    public UnionTripIdCche(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void loadOnStartup() {

        refreshStopIds();
//        refreshTrips();
    }

    public void refreshStopIds() {
        String sql =
                """
                SELECT DISTINCT st.stop_id
                FROM gtfs_trips t
                JOIN gtfs_stop_times st ON st.trip_id = t.trip_id
                JOIN gtfs_stops s ON s.stop_id = st.stop_id
                WHERE t.route_id = ?
                  AND s.stop_name ILIKE ?;
                """;

        List<String> stopIds = jdbcTemplate.queryForList(sql, String.class, routeId, stopName);

        cachedTripIds = Set.copyOf(stopIds);
    }

//    public void refreshTrips() {
//        String sql =
//                """
//                 SELECT t.trip_id
//                 FROM gtfs_trips t
//                 JOIN gtfs_stop_times st_airport
//                    ON st_airport.trip_id = t.trip_id AND st_airport.stop_id = ?
//                 JOIN gtfs_stop_times st_union
//                    ON st_union.trip_id = t.trip_id AND st_union.stop_id = ?
//                 WHERE t.route_id = 'A'
//                    AND st_airport.stop_sequence < st_union.stop_sequence
//                 """;
//
//        List<String> tripIds =
//                jdbcTemplate.queryForList(sql, String.class, AIRPORT_STOP_ID, UNION_STOP_ID);
//
//        cachedTripIds = Set.copyOf(tripIds);
//    }
}
