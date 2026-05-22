package com.mysawit.mysawit_auth.rateLimit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SlidingWindowRateLimiterTest {

    @Test
    void tryConsumeAllowWithinLimit() {
        final SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(5, 60);
        final String key = "192.168.1.1";

        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryConsume(key), "Should allow up to 5 requests");
        }
    }

    @Test
    void tryConsumeBlockAboveLimit() {
        final SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 60);
        final String key = "192.168.1.2";

        assertTrue(limiter.tryConsume(key));
        assertTrue(limiter.tryConsume(key));
        assertTrue(limiter.tryConsume(key));

        assertFalse(limiter.tryConsume(key), "Should block the 4th request");
    }

    @Test
    void tryConsumeConcurrentRequests() throws InterruptedException {
        final SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(10, 60);
        final String key = "192.168.1.3";

        final int threads = 20;
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        final CountDownLatch latch = new CountDownLatch(threads);
        final AtomicInteger allowedRequests = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                if (limiter.tryConsume(key)) {
                    allowedRequests.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(10, allowedRequests.get(), "Should strictly allow 10 concurrent requests");
    }
}
