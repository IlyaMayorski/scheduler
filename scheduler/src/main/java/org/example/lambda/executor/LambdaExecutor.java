package org.example.lambda.executor;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Builder;
import org.example.config.ExecutionConfig;
import org.example.lambda.LambdaRuntimeDefinition;
import org.example.lambda.LambdaStorage;
import org.example.lambda.execution.result.LambdaExecutionResult;
import org.example.lambda.execution.result.LambdaExecutionResultStorage;
import org.example.lambda.execution.status.LambdaExecutionState;
import org.example.lambda.execution.status.LambdaExecutionStatus;
import org.example.lambda.execution.status.LambdaStatusStorage;

public class LambdaExecutor {

  private final AtomicBoolean running = new AtomicBoolean(false);
  private final LambdaStatusStorage lambdaStatusStorage;
  private final LambdaExecutionResultStorage executionResultStorage;
  private final ThreadPoolExecutor executor;
  private final LambdaExecutionContextCacheProvider lambdaExecutionContextCacheProvider;

  private final LambdaStorage lambdaStorage;
  private final LambdaExecutionQueue lambdaExecutionQueue;

  @Builder
  private LambdaExecutor(final LambdaStatusStorage lambdaStatusStorage,
      final LambdaExecutionResultStorage executionResultStorage,
      final LambdaExecutionContextCacheProvider executionContextCacheProvider,
      final ExecutionConfig executionConfig,
      final LambdaStorage lambdaStorage,
      final LambdaExecutionQueue lambdaExecutionQueue) {
    this.lambdaStatusStorage = lambdaStatusStorage;
    this.executionResultStorage = executionResultStorage;
    this.executor = new ThreadPoolExecutor(2,
        executionConfig.getMaxConcurrentExecutions() + 1, 50,
        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    this.lambdaExecutionContextCacheProvider = executionContextCacheProvider;
    this.lambdaStorage = lambdaStorage;
    this.lambdaExecutionQueue = lambdaExecutionQueue;
  }

  private void executeLambda(final LambdaRuntimeDefinition lambdaDefinition) {
    final var executionId = UUID.randomUUID().toString();
    try {
      final var supplier = lambdaExecutionContextCacheProvider.prepareExecutionContext(
          lambdaDefinition);
      lambdaStatusStorage.upsert(
          new LambdaExecutionStatus(lambdaDefinition.getName(), LambdaExecutionState.RUNNING,
              executionId));
      executor.execute(() -> {
        try {
          executionResultStorage.save(
              new LambdaExecutionResult(lambdaDefinition.getName(), executionId, supplier.get()));
          lambdaStatusStorage.upsert(
              new LambdaExecutionStatus(lambdaDefinition.getName(), LambdaExecutionState.SUCCESS,
                  executionId));
        } catch (Exception e) {
          lambdaStatusStorage.upsert(
              new LambdaExecutionStatus(lambdaDefinition.getName(), LambdaExecutionState.FAILED,
                  executionId));
          executionResultStorage.save(
              new LambdaExecutionResult(lambdaDefinition.getName(), executionId, e));
        }
      });
    } catch (Exception e) {
      lambdaStatusStorage.upsert(
          new LambdaExecutionStatus(lambdaDefinition.getName(), LambdaExecutionState.FAILED,
              executionId));
    }
  }

  private void consumeLambdaExecutionRequests() {
    while (running.get()) {
      this.lambdaExecutionQueue.poll().ifPresent(lambdaName -> {
        final var lambdaDefinition = lambdaStorage.getByName(lambdaName);
        if (lambdaDefinition != null) {
          executeLambda(lambdaDefinition);
        }
      });
    }
  }

  public void start() {
    System.out.println("Starting LambdaExecutor main thread");
    running.set(true);
    this.executor.execute(this::consumeLambdaExecutionRequests);
  }

  public void shutdown() {
    System.out.println("Shutting down LambdaExecutor main thread");
    running.set(false);
  }
}
