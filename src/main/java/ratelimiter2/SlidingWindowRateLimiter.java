package ratelimiter2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowRateLimiter implements RateLimiter {

    private final int secondsWindows; // 10
    private final int limit; // 5
    private final Deque<Long> timestamps; // unbounded
    private final ReentrantLock lock;

    public SlidingWindowRateLimiter(int secondsWindows, int limit) {
        this.secondsWindows = secondsWindows;
        this.limit = limit;
        this.timestamps = new ArrayDeque<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public boolean tryAcquire() {
        lock.lock();
        try {
            Long now = System.nanoTime();
            while (!timestamps.isEmpty() && (stalePresent(now))) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= limit) {
                return false;
            }

            timestamps.add(now);
        } finally {
            lock.unlock();
        }
        return true;
    }

    private boolean stalePresent(Long nowNanoTime) {
        long elapsed = nowNanoTime - timestamps.peekFirst();
        return elapsed >= TimeUnit.SECONDS.toNanos(secondsWindows);
    }
}
