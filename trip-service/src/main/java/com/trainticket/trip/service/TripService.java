package com.trainticket.trip.service;

import com.trainticket.common.event.TripCompletedEvent;
import com.trainticket.common.exception.BusinessException;
import com.trainticket.trip.cache.TripCacheService;
import com.trainticket.trip.dto.TripResponse;
import com.trainticket.trip.entity.Trip;
import com.trainticket.trip.entity.TripStatus;
import com.trainticket.trip.entity.Station;
import com.trainticket.trip.kafka.TripEventProducer;
import com.trainticket.trip.repository.StationRepository;
import com.trainticket.trip.repository.TripRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final StationRepository stationRepository;
    private final TripCacheService tripCacheService;
    private final TripEventProducer tripEventProducer;

    public TripResponse checkin(UUID userId, UUID stationId) {
        String activeTrip = tripCacheService.getActiveTripId(userId);
        if (activeTrip != null) {
            throw new BusinessException("ALREADY_CHECKED_IN", "User has an active trip", HttpStatus.CONFLICT);
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new BusinessException("INVALID_STATION", "Station not found", HttpStatus.NOT_FOUND));

        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .checkinStationId(station.getId())
                .checkinTime(Instant.now())
                .status(TripStatus.ACTIVE)
                .build();

        Trip saved = tripRepository.save(trip);
        tripCacheService.setActiveTripId(userId, saved.getId(), Duration.ofHours(4));
        return toResponse(saved);
    }

    public TripResponse checkout(UUID userId, UUID stationId) {
        Trip active = findActiveTrip(userId);
        if (active.getCheckinStationId().equals(stationId)) {
            throw new BusinessException("SAME_STATION", "Checkout station must differ", HttpStatus.BAD_REQUEST);
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new BusinessException("INVALID_STATION", "Station not found", HttpStatus.NOT_FOUND));

        Instant now = Instant.now();
        active.setCheckoutStationId(station.getId());
        active.setCheckoutTime(now);
        active.setDurationMinutes(Duration.between(active.getCheckinTime(), now).toMinutes());
        active.setStatus(TripStatus.PENDING_PAYMENT);

        Trip saved = tripRepository.save(active);
        tripCacheService.clearActiveTripId(userId);

        TripCompletedEvent.TripCompletedPayload payload = TripCompletedEvent.TripCompletedPayload.builder()
                .tripId(saved.getId().toString())
                .userId(saved.getUserId().toString())
                .checkinStationId(saved.getCheckinStationId().toString())
                .checkoutStationId(saved.getCheckoutStationId().toString())
                .checkinTime(saved.getCheckinTime())
                .checkoutTime(saved.getCheckoutTime())
                .durationMinutes(saved.getDurationMinutes() == null ? 0 : saved.getDurationMinutes())
                .build();
        tripEventProducer.publishTripCompleted(TripCompletedEvent.of(payload));

        return toResponse(saved);
    }

    public TripResponse getActiveTrip(UUID userId) {
        Trip trip = findActiveTrip(userId);
        return toResponse(trip);
    }

    public Page<Trip> getHistory(UUID userId, Pageable pageable) {
        return tripRepository.findByUserId(userId, pageable);
    }

    public Trip getTrip(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new BusinessException("TRIP_NOT_FOUND", "Trip not found", HttpStatus.NOT_FOUND));
    }

    private Trip findActiveTrip(UUID userId) {
        String activeTripId = tripCacheService.getActiveTripId(userId);
        if (activeTripId != null) {
            return tripRepository.findById(UUID.fromString(activeTripId))
                    .orElseThrow(() -> new BusinessException("NO_ACTIVE_TRIP", "No active trip", HttpStatus.NOT_FOUND));
        }

        return tripRepository.findFirstByUserIdAndStatus(userId, TripStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("NO_ACTIVE_TRIP", "No active trip", HttpStatus.NOT_FOUND));
    }

    private TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .tripId(trip.getId())
                .checkinStationId(trip.getCheckinStationId())
                .checkoutStationId(trip.getCheckoutStationId())
                .checkinTime(trip.getCheckinTime())
                .checkoutTime(trip.getCheckoutTime())
                .durationMinutes(trip.getDurationMinutes())
                .status(trip.getStatus().name())
                .build();
    }
}
