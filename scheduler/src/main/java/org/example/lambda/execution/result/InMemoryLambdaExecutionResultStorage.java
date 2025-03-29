package org.example.lambda.execution.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryLambdaExecutionResultStorage implements LambdaExecutionResultStorage {

  final List<LambdaExecutionResult> lambdaResult = Collections.synchronizedList(new ArrayList<>());

  @Override
  public void save(final LambdaExecutionResult result) {
    lambdaResult.add(result);
  }

  @Override
  public List<LambdaExecutionResult> findByName(final String name) {
    return lambdaResult.stream()
        .filter(entry -> entry.name().equals(name))
        .collect(Collectors.toList());
  }

}
