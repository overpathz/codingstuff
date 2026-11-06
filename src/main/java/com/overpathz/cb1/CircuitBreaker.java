package com.overpathz.cb1;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class CircuitBreaker implements ICircuitBreaker {

    private final String name;
    private int failuresCounter;
    public final int failureThreshold; // 3
    public final int openDuration;
    public CircuitBreakerState state;
    public long openedAt; // nanos

    public CircuitBreaker(String name, int failureThreshold, int openDuration) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
        this.state = CircuitBreakerState.CLOSED;
    }

    @Override
    public <T> T execute(Callable<T> callable) throws Exception {
        /*
            "CLOSED" -> any call ->  onSuccess -> doNothing (clear failuresCounter, )
                                   onFailure -> ++failuresCounter -> check with failureThreshold
                                   -> "OPEN" (set openAt)
                                     -> AFTER some time if 10 sec passed away, we move to half_open -> making a call
                                     -> CLOSED, fail -> OPEN, openAt
         */
        beforeCall();

        T result;
        try {
            result = callable.call();
        } catch (Exception e) {
            onFailure();
            throw e;
        }

        onSuccess();
        return result;
    }

    private void beforeCall() throws CallNotPermittedException {
        switch (state) {
            case CLOSED -> {  }
            case OPEN -> {
                long elapsed = System.nanoTime() - openedAt;
                if (elapsed >= TimeUnit.SECONDS.toNanos(openDuration)) {
                    this.state = CircuitBreakerState.HALF_OPEN;
                } else {
                    throw new CallNotPermittedException();
                }
            }
            case HALF_OPEN -> throw new CallNotPermittedException();
        }
    }

    private void onSuccess() {
        if (isHalfOpen()) {
            this.state = CircuitBreakerState.CLOSED;
            this.failuresCounter = 0;
            return;
        }
        if (isClosed()) {
            this.failuresCounter = 0;
        }
    }

    private void onFailure() {
        if (isHalfOpen()) {
            this.openedAt = System.nanoTime();
            this.state = CircuitBreakerState.OPEN;
            return;
        }
        if (isClosed()) {
            if (++failuresCounter >= failureThreshold) {
                this.state = CircuitBreakerState.OPEN;
                this.openedAt = System.nanoTime();
            }
        }
    }

    private boolean isOpen() {
        return this.state == CircuitBreakerState.OPEN;
    }

    private boolean isHalfOpen() {
        return this.state == CircuitBreakerState.HALF_OPEN;
    }

    private boolean isClosed() {
        return this.state == CircuitBreakerState.CLOSED;
    }
}
