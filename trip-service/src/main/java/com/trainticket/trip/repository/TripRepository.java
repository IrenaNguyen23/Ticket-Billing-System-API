package com.trainticket.trip.repository;

import com.trainticket.trip.entity.Trip;
import com.trainticket.trip.entity.TripStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    Optional<Trip> findFirstByUserIdAndStatus(UUID userId, TripStatus status);
    Page<Trip> findByUserId(UUID userId, Pageable pageable);
}
