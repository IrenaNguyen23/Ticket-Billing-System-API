package com.trainticket.trip.cache;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripCacheService {
    private final StringRedisTemplate redisTemplate;

    public String getActiveTripId(UUID userId) {
        return redisTemplate.opsForValue().get(keyActiveTrip(userId));
    }

    public void setActiveTripId(UUID userId, UUID tripId, Duration ttl) {
        redisTemplate.opsForValue().set(keyActiveTrip(userId), tripId.toString(), ttl);
    }

    public void clearActiveTripId(UUID userId) {
        redisTemplate.delete(keyActiveTrip(userId));
    }

    private String keyActiveTrip(UUID userId) {
        return "active_trip:" + userId;
    }
}
