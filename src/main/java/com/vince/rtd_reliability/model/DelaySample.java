package com.vince.rtd_reliability.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "delay_samples")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DelaySample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_id", nullable = false)
    private String routeId;

    @Column(name = "trip_id", nullable = false)
    private String tripId;

    @Column(name = "delay_seconds", nullable = false)
    private Integer delaySeconds;

    @Column(name = "sampled_at", nullable = false)
    private Instant sampledAt;
}
