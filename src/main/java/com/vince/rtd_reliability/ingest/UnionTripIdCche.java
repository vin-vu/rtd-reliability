package com.vince.rtd_reliability.ingest;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class UnionTripIdCche {

    private static final String routeId = "15";
    private static final String stopName = "%union%";
    private static final String AIRPORT_STOP_ID = "34476";
    private static final String UNION_STOP_ID = "34667";
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Getter private volatile Set<String> cachedStopIds = Set.of();
    @Getter private volatile Set<String> cachedTripIds = Set.of();

    public UnionTripIdCche(
            JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @PostConstruct
    public void loadOnStartup() {
        refreshStopIds();
        log.info("cached trip ids: {}", cachedStopIds);
        //        refreshTrips();

        log.info("cached stop ids: {}", cachedStopIds);
    }

    public void refreshStopIds() {
        String sql =
                """
                SELECT DISTINCT st.stop_id
                FROM gtfs_trips t
                JOIN gtfs_stop_times st
                  ON st.trip_id = t.trip_id
                JOIN gtfs_stops s
                  ON s.stop_id = st.stop_id
                WHERE t.route_id = ?
                  AND s.stop_name ILIKE ?;
                """;

        List<String> stopIds = jdbcTemplate.queryForList(sql, String.class, routeId, stopName);

        cachedStopIds = Set.copyOf(stopIds);
    }

    //    public void refreshTrips() {
    //        String sql =
    //                """
    //                SELECT DISTINCT t.trip_id
    //                FROM gtfs_trips t
    //                JOIN gtfs_stop_times st ON st.trip_id = t.trip_id
    //                WHERE t.route_id = :routeId
    //                  AND st.stop_id IN (:stopIds);
    //                """;
    //
    //        Map<String, Object> params =
    //                Map.of(
    //                        "routeId", routeId,
    //                        "stopIds", cachedStopIds);
    //
    //        List<String> tripIds = namedParameterJdbcTemplate.queryForList(sql, params,
    // String.class);
    //
    //        cachedTripIds = Set.copyOf(tripIds);
    //    }
}
