package org.example;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Builder;
import org.example.config.ExecutionConfig;
import org.example.config.StorageConfig;
import org.example.lambda.InMemoryLambdaStorage;
import org.example.lambda.LambdaDefinition;
import org.example.lambda.LambdaRuntimeDefinition;
import org.example.lambda.LambdaStorage;
import org.example.lambda.execution.result.InMemoryLambdaExecutionResultStorage;
import org.example.lambda.execution.result.LambdaExecutionResult;
import org.example.lambda.execution.result.LambdaExecutionResultStorage;
import org.example.lambda.execution.status.InMemoryLambdaStatusStorage;
import org.example.lambda.execution.status.LambdaExecutionStatus;
import org.example.lambda.execution.status.LambdaStatusStorage;
import org.example.lambda.executor.LambdaExecutionContextCacheProvider;
import org.example.lambda.executor.LambdaExecutor;
import org.example.lambda.trigger.InMemoryTriggerStorage;
import org.example.lambda.trigger.Trigger;
import org.example.lambda.trigger.TriggerStorage;
import org.example.lambda.utils.LambdaUtils;


public class Scheduler {

  private final AtomicBoolean running = new AtomicBoolean(true);
  private final LambdaExecutor lambdaExecutor;
  private LambdaStatusStorage lambdaStatusStorage;
  private LambdaExecutionResultStorage lambdaResultStorage;
  private TriggerStorage triggerStorage;
  private LambdaStorage lambdaStorage;

  @Builder
  private Scheduler(final ExecutionConfig executionConfig,
      final StorageConfig storageConfig) {
    this.initializeStorage(storageConfig);
    this.lambdaExecutor = LambdaExecutor.builder()
        .executionConfig(executionConfig)
        .executionContextCacheProvider(new LambdaExecutionContextCacheProvider())
        .lambdaStatusStorage(this.lambdaStatusStorage)
        .executionResultStorage(this.lambdaResultStorage)
        .build();
  }

  private void initializeStorage(final StorageConfig storageConfig) {
    switch (storageConfig) {
      case IN_MEMORY -> initializeInMemory();
      default -> throw new IllegalArgumentException("Unsupported storage type");
    }
  }

  private void initializeInMemory() {
    this.lambdaStatusStorage = new InMemoryLambdaStatusStorage();
    this.lambdaResultStorage = new InMemoryLambdaExecutionResultStorage();
    this.triggerStorage = new InMemoryTriggerStorage();
    this.lambdaStorage = new InMemoryLambdaStorage();
  }

  public void createTrigger(final Trigger trigger) {
    this.triggerStorage.save(trigger);
  }

  public void createLambda(final LambdaDefinition lambdaDefinition) {
    try {
      this.lambdaStorage.save(LambdaRuntimeDefinition
          .builder()
          .jar(LambdaUtils.getJarFromLambdaDefinition(lambdaDefinition.getZip()))
          .name(lambdaDefinition.getName())
          .mainClass(lambdaDefinition.getMainClass())
          .build());
    } catch (IOException e) {
      throw new RuntimeException("Zip does not contain JAR file");
    }
  }

  public void start() {
    new Thread(() -> {
      while (running.get()) {
        final var triggers = this.triggerStorage.getFiredTriggers();
        triggers.forEach(trigger -> {
          lambdaExecutor.executeLambda(lambdaStorage.getByName(trigger.getJobName()));
          trigger.updateNextExecution();
        });
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          System.err.println("Scheduler shutting down...");
          Thread.currentThread().interrupt();
          break;
        }
      }
    }).start();
  }

  public void stop() {
    running.set(false);
  }

  public List<LambdaExecutionResult> getExecutionResults(final String lambdaName) {
    return lambdaResultStorage.findByName(lambdaName);
  }

  public List<LambdaExecutionStatus> getExecutionStatuses(final String lambdaName) {
    return lambdaStatusStorage.findByName(lambdaName);
  }
}
