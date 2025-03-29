package org.example.lambda.execution.result;

import java.util.List;

public interface LambdaExecutionResultStorage {

  void save(LambdaExecutionResult result);

  List<LambdaExecutionResult> findByName(final String name);
}
