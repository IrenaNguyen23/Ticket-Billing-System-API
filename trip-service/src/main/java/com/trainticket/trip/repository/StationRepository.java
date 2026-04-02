package com.trainticket.trip.repository;

import com.trainticket.trip.entity.Station;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, UUID> {
}
