package org.example.lambda.execution.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryLambdaStatusStorage implements LambdaStatusStorage {

  final List<LambdaExecutionStatus> lambdaStatus = Collections.synchronizedList(new ArrayList<>());

  @Override
  public void upsert(final LambdaExecutionStatus status) {
    lambdaStatus.removeIf(entry ->
        entry.name().equals(status.name()) && entry.executionId().equals(status.executionId()));
    lambdaStatus.add(status);
  }

  @Override
  public List<LambdaExecutionStatus> findByName(String name) {
    return
        lambdaStatus.stream()
            .filter(entry -> entry.name().equals(name))
            .collect(Collectors.toList());
  }
}
