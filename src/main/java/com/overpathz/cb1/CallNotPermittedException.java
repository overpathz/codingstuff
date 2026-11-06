package com.overpathz.cb1;

public class CallNotPermittedException extends Exception {
    public CallNotPermittedException() {
        super("Circuit breaker in OPEN state, call not permitted");
    }
}
