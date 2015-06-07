package net.michaeljohansen;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.concurrent.Future;

/**
 * Created by Michael on 04.06.2015.
 */
public class AsyncFactory {
    public <T> Class<T> getModifiedClass(Class<T> clazz) {
        return (Class<T>) new ByteBuddy()
                .subclass(clazz)
                .method(ElementMatchers.isAnnotatedWith(Retry.class))
                .intercept(MethodDelegation.to(RetryImplementation.class))
                .method(ElementMatchers
                        .isAnnotatedWith(Async.class)
                        .and(ElementMatchers.returns(Future.class)))
                .intercept(MethodDelegation.to(AsyncImplementation.class))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }
}
