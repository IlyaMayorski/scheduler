package org.example.lambda.execution.status;

import java.util.List;

public interface LambdaStatusStorage {

  void upsert(final LambdaExecutionStatus status);

  List<LambdaExecutionStatus> findByName(final String name);
}
