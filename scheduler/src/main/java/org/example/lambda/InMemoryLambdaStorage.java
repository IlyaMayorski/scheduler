package org.example.lambda;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLambdaStorage implements LambdaStorage {

  private final Map<String, LambdaRuntimeDefinition> jobs = new ConcurrentHashMap<>();

  @Override
  public void save(LambdaRuntimeDefinition lambdaDefinition) {
    jobs.put(lambdaDefinition.getName(), lambdaDefinition);
  }

  @Override
  public LambdaRuntimeDefinition getByName(String name) {
    return jobs.get(name);
  }
}
