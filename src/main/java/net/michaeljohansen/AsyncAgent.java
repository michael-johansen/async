package net.michaeljohansen;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Future;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Created by Michael on 01.06.2015.
 */
public class AsyncAgent {

    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .rebase(not(nameStartsWith("org.groovy")))
                .transform((builder, typeDescription) ->
                        builder
                                .method(isAnnotatedWith(Retry.class))
                                .intercept(MethodDelegation.to(RetryImplementation.class))

                                .method(isAnnotatedWith(Async.class)
                                        .and(returns(Future.class)))
                                .intercept(MethodDelegation.to(AsyncImplementation.class)))
                .installOn(instrumentation);
    }
}
