package com.trainticket.trip.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class CheckinRequest {
    @NotNull
    private UUID stationId;
}
