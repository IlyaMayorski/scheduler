package org.example;

import java.util.Map;
import java.util.function.Supplier;

public class ExampleLambdaFailure implements Supplier<Map<String, Object>> {

  @Override
  public Map<String, Object> get() {
    throw new RuntimeException("Meh.");
  }
}
