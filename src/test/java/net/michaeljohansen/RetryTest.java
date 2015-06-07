package net.michaeljohansen;

import org.junit.Before;
import org.junit.Test;

import static net.bytebuddy.matcher.ElementMatchers.inheritsAnnotation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Created by Michael on 01.06.2015.
 */
public class RetryTest {
    private AsyncFactory asyncFactory;

    @Before
    public void setUp() throws Exception {
        asyncFactory = new AsyncFactory();
    }

    @Test
    public void succeedsWithEnoughAttempts() throws Exception {
        Class<FailNTimesBeforeSuccess> clazz = asyncFactory.getModifiedClass(FailNTimesBeforeSuccess.class);
        FailNTimesBeforeSuccess failNTimesBeforeSuccess = clazz
                .getConstructor(int.class)
                .newInstance(5);

        failNTimesBeforeSuccess.check();
    }

    @Test(expected = FailNTimesBeforeSuccessException.class)
    public void failsWithTooFewAttempts() throws Exception {
        Class<FailNTimesBeforeSuccess> clazz = asyncFactory.getModifiedClass(FailNTimesBeforeSuccess.class);
        FailNTimesBeforeSuccess failNTimesBeforeSuccess = clazz
                .getConstructor(int.class)
                .newInstance(15);

        failNTimesBeforeSuccess.check();
    }

    public static class FailNTimesBeforeSuccess {
        private int failedCount = 0;
        private int failedCountTarget;

        public FailNTimesBeforeSuccess(int failedCountTarget) {
            this.failedCountTarget = failedCountTarget;
        }

        @Retry(maximumAttempts = 10, maximumAttemptsExceededException = FailNTimesBeforeSuccessException.class)
        public void check() {
            if (failedCount++ < failedCountTarget) throw new IllegalStateException();
        }
    }

    public static class FailNTimesBeforeSuccessException extends Exception {
        public FailNTimesBeforeSuccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}