package com.trainticket.trip.controller;

import com.trainticket.common.dto.ApiResponse;
import com.trainticket.trip.dto.CheckinRequest;
import com.trainticket.trip.dto.CheckoutRequest;
import com.trainticket.trip.dto.TripResponse;
import com.trainticket.trip.entity.Trip;
import com.trainticket.trip.service.TripService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping("/checkin")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TripResponse> checkin(Authentication authentication,
                                             @Valid @RequestBody CheckinRequest request) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(tripService.checkin(userId, request.getStationId()));
    }

    @PostMapping("/checkout")
    public ApiResponse<TripResponse> checkout(Authentication authentication,
                                              @Valid @RequestBody CheckoutRequest request) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(tripService.checkout(userId, request.getStationId()));
    }

    @GetMapping("/active")
    public ApiResponse<TripResponse> active(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ApiResponse.ok(tripService.getActiveTrip(userId));
    }

    @GetMapping("/history")
    public ApiResponse<Page<TripResponse>> history(Authentication authentication, Pageable pageable) {
        UUID userId = UUID.fromString(authentication.getName());
        Page<TripResponse> mapped = tripService.getHistory(userId, pageable)
                .map(trip -> TripResponse.builder()
                        .tripId(trip.getId())
                        .checkinStationId(trip.getCheckinStationId())
                        .checkoutStationId(trip.getCheckoutStationId())
                        .checkinTime(trip.getCheckinTime())
                        .checkoutTime(trip.getCheckoutTime())
                        .durationMinutes(trip.getDurationMinutes())
                        .status(trip.getStatus().name())
                        .build());
        return ApiResponse.ok(mapped);
    }

    @GetMapping("/{tripId}")
    public ApiResponse<TripResponse> getTrip(@PathVariable UUID tripId) {
        Trip trip = tripService.getTrip(tripId);
        TripResponse response = TripResponse.builder()
                .tripId(trip.getId())
                .checkinStationId(trip.getCheckinStationId())
                .checkoutStationId(trip.getCheckoutStationId())
                .checkinTime(trip.getCheckinTime())
                .checkoutTime(trip.getCheckoutTime())
                .durationMinutes(trip.getDurationMinutes())
                .status(trip.getStatus().name())
                .build();
        return ApiResponse.ok(response);
    }
}
