package com.vince.rtd_reliability.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "delay_samples")
@Getter
@NoArgsConstructor
public class DelaySample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String routeId;
    private String tripId;
    private String stopId;

    private long delaySeconds;
    private Instant sampledAt;

    public DelaySample(String routeId, String tripId, String stopId, long delaySeconds, Instant sampledAt) {
        this.routeId = routeId;
        this.tripId = tripId;
        this.delaySeconds = delaySeconds;
        this.sampledAt = sampledAt;
    }
}
