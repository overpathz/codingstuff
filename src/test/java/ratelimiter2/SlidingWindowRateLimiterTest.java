package ratelimiter2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SlidingWindowRateLimiterTest {

    private static final int LIMIT = 3;
    private static final int SECONDS_WINDOW = 5;

    @Test
    void shouldAllowRequestsWithinLimit() {
        final RateLimiter rl = new SlidingWindowRateLimiter(SECONDS_WINDOW, LIMIT);
        for (int i = 0; i < LIMIT; i++) assertTrue(rl.tryAcquire());
    }

    @Test
    void shouldRejectRequestsWhenLimitIsReached() {
        final RateLimiter rl = new SlidingWindowRateLimiter(SECONDS_WINDOW, LIMIT);
        for (int i = 0; i < LIMIT; i++) {
            assertTrue(rl.tryAcquire());
        }
        assertFalse(rl.tryAcquire());
    }

    @Test
    void shouldAllowRequestsAfterWindowExpires() {

    }

    @Test
    void shouldRemoveExpiredTimestamps() {
    }

    @Test
    void shouldNotCountRejectedRequests() {
    }

    @Test
    void shouldAllowOnlyUpToLimitUnderConcurrentRequests() {
    }

    @Test
    void shouldRejectInvalidWindowDuration() {
    }

    @Test
    void shouldRejectInvalidLimit() {
    }
}