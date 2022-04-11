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
  public DemoLoggerInstrumentationModule() {
    super("slf4j-api", "slf4j-api-1.7.31");
  }

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
    return singletonList(new DemoLoggerInstrumentation());
  }
}
