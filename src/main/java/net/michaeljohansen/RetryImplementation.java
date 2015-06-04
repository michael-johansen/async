package net.michaeljohansen;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * TODO: Retry after progressively increasing time.
 * TODO: Scheduling
 */
public class RetryImplementation {
    private final static Logger LOGGER = LoggerFactory.getLogger(RetryImplementation.class);

    public static <T> T retry(
            @SuperCall Callable<T> zuper,
            @Origin Class<?> clazz,
            @Origin Method method)
            throws Exception {

        Retry retry = method.getAnnotation(Retry.class);

        Optional<Exception> lastException = Optional.empty();

        for (int i = 1; i <= retry.maximumAttempts(); i++) {
            LOGGER.debug("Attempt " + i + "/" + retry.maximumAttempts());
            try {
                return zuper.call();
            } catch (Exception e) {
                LOGGER.debug("Call failed with: " + e.getMessage());
                lastException = Optional.ofNullable(e);
            }
        }

        throw retry.maximumAttemptsExceededException()
                .getConstructor(String.class, Throwable.class)
                .newInstance(
                        String.format(
                                retry.maximumAttemptsExceededMessage(),
                                retry.maximumAttempts()
                        ),
                        lastException.get()
                );
    }
}