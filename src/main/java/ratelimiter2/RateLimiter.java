package ratelimiter2;

public interface RateLimiter {
    boolean tryAcquire();
}