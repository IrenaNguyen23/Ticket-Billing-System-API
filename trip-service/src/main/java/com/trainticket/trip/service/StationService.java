package com.trainticket.trip.service;

import com.trainticket.trip.entity.Station;
import com.trainticket.trip.repository.StationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
}
