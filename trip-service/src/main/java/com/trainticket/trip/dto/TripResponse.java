package com.trainticket.trip.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TripResponse {
    private UUID tripId;
    private UUID checkinStationId;
    private UUID checkoutStationId;
    private Instant checkinTime;
    private Instant checkoutTime;
    private Long durationMinutes;
    private String status;
}
