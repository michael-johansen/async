package net.michaeljohansen;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

/**
 * Created by Michael on 07.06.2015.
 */
public class AsyncTest {

    private AsyncFactory asyncFactory;

    @Before
    public void setUp() throws Exception {
        asyncFactory = new AsyncFactory();
    }

    @Test
    public void canRunTaskInBackground() throws Exception {
        Class<LongRunningTaskExecutor> modifiedClass = asyncFactory.getModifiedClass(LongRunningTaskExecutor.class);
        LongRunningTaskExecutor longRunningTaskExecutor = modifiedClass
                .getConstructor(int.class)
                .newInstance(2000);


        Future<Boolean> future = assertExecutionTime(
                () -> longRunningTaskExecutor.executeTaskAsync(),
                Duration.ofMillis(0),
                Duration.ofMillis(250)
        );

        Boolean result = assertExecutionTime(
                () -> future.get(),
                Duration.ofMillis(1500),
                Duration.ofMillis(2500)
        );

        assertTrue("Result was not expected value", result);
    }

    private static <T> T assertExecutionTime(
            ThrowingSupplier<T> function,
            Duration minDuration,
            Duration maxDuration)
            throws Exception {
        Clock clock = Clock.systemUTC();

        Instant start = clock.instant();
        T t = function.get();
        Instant end = clock.instant();

        Duration duration = Duration.between(start, end);

        assertTrue(
                "Execution time should be larger than " + minDuration.toMillis() + "ms, but was " + duration.toMillis() + "ms",
                duration.compareTo(minDuration) > 0
        );
        assertTrue(
                "Execution time should be less than " + maxDuration.toMillis() + "ms, but was " + duration.toMillis() + "ms"
                , duration.compareTo(maxDuration) < 0
        );

        return t;
    }

    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    public static class LongRunningTaskExecutor {
        private final int sleepTime;

        public LongRunningTaskExecutor(int sleepTime) {
            this.sleepTime = sleepTime;
        }

        @Async
        public Future<Boolean> executeTaskAsync() throws InterruptedException {
            Thread.sleep(sleepTime);
            return CompletableFuture.completedFuture(true);
        }
    }
}
