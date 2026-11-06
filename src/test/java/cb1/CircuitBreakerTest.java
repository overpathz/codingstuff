package cb1;

import com.overpathz.cb1.CallNotPermittedException;
import com.overpathz.cb1.CircuitBreaker;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CircuitBreakerTest {

    @Test
    void shouldOpenAfterThreeFailures() throws Exception {
        CircuitBreaker cb = new CircuitBreaker("visa", 3, 10);
        AtomicInteger callCounter = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            assertThrows(RuntimeException.class, () ->
                    cb.execute(() -> {
                        callCounter.incrementAndGet();
                        throw new RuntimeException();
                    })
            );
        }

        assertThrows(CallNotPermittedException.class, () ->
                cb.execute(() -> {
                    callCounter.incrementAndGet();
                    return "SUCCESS";
                })
        );

        assertEquals(3, callCounter.get());
    }
}