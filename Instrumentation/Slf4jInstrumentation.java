/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.isNamed;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class Slf4jInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<ClassLoader> classLoaderOptimization() {
    return hasClassesNamed(new StringBuilder("org.").append("slf4j.Logger").toString());
  }
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return implementsInterface(named(new StringBuilder("org.").append("slf4j.Logger").toString()));
  }

  @Override
  public void transform(TypeTransformer typeTransformer) {
    typeTransformer.applyAdviceToMethod(
        named("error")
            .and(
                takesArgument(
                    0, named("java.lang.String")))
            .and(
                takesArgument(
                    1, named("java.lang.Throwable")))
            .and(isPublic()),
        this.getClass().getName() + "$LoggerAdvice");
  }

  @SuppressWarnings("unused")
  public static class LoggerAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.Argument(0) String  message,@Advice.Argument(1) Throwable  throwable){
      /*
      instead of span.current use Java8BytecodeBridge.currentSpan()
       */
      Java8BytecodeBridge.currentSpan().setAttribute("error",true);
      Java8BytecodeBridge.currentSpan().recordException(throwable);
      /*
      Span.current().recordException(throwable);
      Java8BytecodeBridge.currentSpan().setAttribute("TESTING","HI THERE");
       */


      }
    }
  }
