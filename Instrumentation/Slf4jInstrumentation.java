/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.implementsInterface;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class DemoServlet3Instrumentation implements TypeInstrumentation {
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
            .and(isPublic()),
        this.getClass().getName() + "$DemoServlet3Advice");
  }

  @SuppressWarnings("unused")
  public static class DemoServlet3Advice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.AllArguments Object[] args){
      /*
      instead of span.current use Java8BytecodeBridge.currentSpan()

      Instead of this-----> Span.current().recordException(throwable);
      Use This------> Java8BytecodeBridge.currentSpan().recordException(throwable)

       */
      for(Object arg:args)
      {
        if (arg instanceof Throwable) {
          Throwable throwable = (Throwable) arg;
          Java8BytecodeBridge.currentSpan().setAttribute("error",true);
          Java8BytecodeBridge.currentSpan().recordException(throwable);

        }
      }
      }
    }
  }
