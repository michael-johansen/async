package net.michaeljohansen;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Created by Michael on 01.06.2015.
 */
public class AsyncAgent {

    public static void premain(String arguments, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .rebase(not(nameStartsWith("org.groovy")))
                .transform((builder, typeDescription) ->
                        builder.method(isAnnotatedWith(Retry.class))
                                .intercept(MethodDelegation.to(RetryImplementation.class)))
                .installOn(instrumentation);
    }
}
