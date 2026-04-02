package com.trainticket.trip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "checkin_station_id", nullable = false)
    private UUID checkinStationId;

    @Column(name = "checkout_station_id")
    private UUID checkoutStationId;

    @Column(name = "checkin_time", nullable = false)
    private Instant checkinTime;

    @Column(name = "checkout_time")
    private Instant checkoutTime;

    @Column(name = "duration_minutes")
    private Long durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
