package com.trainticket.common.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    private Instant timestamp;
    private String requestId;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build();
    }

    public static ApiResponse<Void> fail(String code, String message, Map<String, Object> details) {
        return ApiResponse.<Void>builder()
                .success(false)
                .error(new ApiError(code, message, details))
                .timestamp(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build();
    }
}
