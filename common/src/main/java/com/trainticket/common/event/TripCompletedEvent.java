package com.trainticket.common.event;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripCompletedEvent {
    private String eventId;
    private String eventType;
    private Instant timestamp;
    private TripCompletedPayload payload;

    public static TripCompletedEvent of(TripCompletedPayload payload) {
        return TripCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TRIP_COMPLETED")
                .timestamp(Instant.now())
                .payload(payload)
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripCompletedPayload {
        private String tripId;
        private String userId;
        private String checkinStationId;
        private String checkoutStationId;
        private Instant checkinTime;
        private Instant checkoutTime;
        private long durationMinutes;
    }
}
