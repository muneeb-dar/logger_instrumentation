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

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class DemoLoggerInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.slf4j.Logger");
  }

  @Override
  public void transform(TypeTransformer typeTransformer) {
    typeTransformer.applyAdviceToMethod(
        namedOneOf("error", "debug")
            .and(
                ElementMatchers.takesArgument(
                    0, named("java.lang.String")))
            .and(
                ElementMatchers.takesArgument(
                    1, named("java.lang.Throwable")))
            .and(ElementMatchers.isPublic()),
        this.getClass().getName() + "$DemoServlet3Advice");
  }

  @SuppressWarnings("unused")
  public static class DemoServlet3Advice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.Argument(value = 0) String  message,@Advice.Argument(value = 1) Throwable  throwable){
      Span.current().recordException(throwable);
      Java8BytecodeBridge.currentSpan().setAttribute("TESTING","HI THERE");


      }
    }
  }
