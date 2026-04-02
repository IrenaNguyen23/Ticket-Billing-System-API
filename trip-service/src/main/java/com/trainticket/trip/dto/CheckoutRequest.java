package com.trainticket.trip.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull
    private UUID stationId;
}
