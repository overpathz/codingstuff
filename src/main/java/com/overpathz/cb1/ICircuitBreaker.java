package com.overpathz.cb1;

import java.util.concurrent.Callable;

public interface ICircuitBreaker {
    <T> T execute(Callable<T> callable) throws Exception;
}
