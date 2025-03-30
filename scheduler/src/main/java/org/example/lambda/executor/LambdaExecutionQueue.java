package org.example.lambda.executor;

import java.util.Optional;

public interface LambdaExecutionQueue {

  void add(final String lambdaName);

  Optional<String> poll();
}
