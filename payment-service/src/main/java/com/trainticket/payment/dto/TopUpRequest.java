package com.trainticket.payment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TopUpRequest {
    @Min(10000)
    @Max(5000000)
    private long amount;
}
