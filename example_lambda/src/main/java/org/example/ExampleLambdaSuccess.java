package org.example;

import java.util.Map;
import java.util.function.Supplier;

public class ExampleLambdaSuccess implements Supplier<Map<String, Object>> {

  @Override
  public Map<String, Object> get() {
    return Map.of("message", "Hello world!");
  }
}
