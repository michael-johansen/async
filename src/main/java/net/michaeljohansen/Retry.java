package net.michaeljohansen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Michael on 01.06.2015.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    int maximumAttempts() default 10;
    String maximumAttemptsExceededMessage() default "Maximum number of attempts (%s) exceeded.";
    Class<? extends Exception> maximumAttemptsExceededException() default IllegalStateException.class;
}
