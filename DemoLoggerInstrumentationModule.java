/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.javaagent.instrumentation;

import static java.util.Collections.singletonList;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.List;

/**
 * This is a demo instrumentation
 */
@AutoService(InstrumentationModule.class)
public final class DemoLoggerInstrumentationModule extends InstrumentationModule {
  public DemoServlet3InstrumentationModule() {
    super("slf4j-api", "slf4j-api-1.7.31");
  }

  /*
  We want this instrumentation to be applied after the standard servlet instrumentation.
  The latter creates a server span around http request.
  This instrumentation needs access to that server span.
   */
//  @Override
//  public int order() {
//    return 1;
//  }

//  @Override
//  public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
//    return AgentElementMatchers.hasClassesNamed("org.slf4j.Logger");
//  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return singletonList(new DemoServlet3Instrumentation());
  }
}
