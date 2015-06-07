package net.michaeljohansen;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Created by Michael on 07.06.2015.
 */
public class AsyncImplementation {
    private static final Object[] lock = new Object[0];
    private static ThreadPoolExecutor threadPoolExecutor;

    public static <T> Future<T> async(
            @SuperCall Callable<Future<T>> zuper,
            @Origin Class<?> clazz,
            @Origin Method method)
            throws Exception {

        Async async = method.getAnnotation(Async.class);

        CompletableFuture completableFuture = new CompletableFuture();

        getThreadPoolExecutor().execute(() -> {
            try {
                Future<T> result = zuper.call();
                completableFuture.complete(result.get());
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        return completableFuture;
    }

    private static ThreadPoolExecutor getThreadPoolExecutor() {
        if(threadPoolExecutor == null) {
            synchronized (lock) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(10, 50, 5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1024));
                }
            }
        }
        return threadPoolExecutor;
    }
}
