package com.mysawit.mysawit_auth.rateLimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SlidingWindowRateLimiter implements RateLimiter {

    private final Map<String, Deque<Instant>> requestLog = new ConcurrentHashMap<>();

    private final int maxRequests;
    private final long windowSeconds;

    public SlidingWindowRateLimiter(
            @Value("${app.rate-limit.max-requests}") final int maxRequests,
            @Value("${app.rate-limit.window-seconds}") final long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    @Override
    public boolean tryConsume(final String key) {
        final Deque<Instant> timestamps = requestLog.computeIfAbsent(key, k -> new ArrayDeque<>());
        final Instant now = Instant.now();
        final Instant windowStart = now.minusSeconds(windowSeconds);

        synchronized (timestamps) {
            evictOldTimestamps(timestamps, windowStart);

            if (timestamps.size() >= maxRequests) {
                return false;
            }

            timestamps.addLast(now);
            requestLog.put(key, timestamps);

            return true;
        }
    }

    private void evictOldTimestamps(final Deque<Instant> timestamps, final Instant windowStart) {
        while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(windowStart)) {
            timestamps.pollFirst();
        }
    }

}