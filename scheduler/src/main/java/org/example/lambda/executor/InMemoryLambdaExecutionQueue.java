package org.example.lambda.executor;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InMemoryLambdaExecutionQueue implements LambdaExecutionQueue {

  final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

  @Override
  public void add(String lambdaName) {
    queue.add(lambdaName);
  }

  @Override
  public Optional<String> poll() {
    return Optional.ofNullable(queue.poll());
  }
}
