package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.example.config.ExecutionConfig;
import org.example.config.StorageConfig;
import org.example.lambda.LambdaDefinition;
import org.example.lambda.execution.status.LambdaExecutionState;
import org.example.lambda.trigger.CronTrigger;
import org.example.lambda.trigger.OneTimeTrigger;
import org.junit.jupiter.api.Test;

public class SchedulerFunctionalTest {

  @Test
  public void canExecuteCronTriggerSuccess() throws Exception {
    final var scheduler = Scheduler.builder()
        .storageConfig(StorageConfig.IN_MEMORY)
        .executionConfig(ExecutionConfig.withMaxConcurrentExecutions(1))
        .build();

    final var zipFilePath = "../resources/example_lambda-1.0-SNAPSHOT.zip";
    final var bytes = Files.readAllBytes(Paths.get(zipFilePath));
    final var name = UUID.randomUUID().toString();

    final var lambdaDefinition = LambdaDefinition.builder()
        .mainClass("org.example.ExampleLambdaSuccess")
        .name(name)
        .zip(bytes)
        .build();

    final var trigger = CronTrigger.builder()
        .jobName(name)
        .cronExpression("*/1 * * * * *")
        .build();

    scheduler.createLambda(lambdaDefinition);
    scheduler.createTrigger(trigger);
    scheduler.start();

    Awaitility.await()
        .atMost(Duration.ofMillis(2500))
        .untilAsserted(() ->
        {
          assertEquals(scheduler.getExecutionStatuses(name).size(), 2);
          assertEquals(scheduler.getExecutionStatuses(name).get(0).state(),
              LambdaExecutionState.SUCCESS);
          assertEquals(scheduler.getExecutionResults(name).size(), 2);
          assertTrue(Map.class.isAssignableFrom(
              scheduler.getExecutionResults(name).get(0).result().getClass()));
          assertTrue(((Map) scheduler.getExecutionResults(name).get(0).result()).get("message")
              .equals("Hello world!"));
        });
  }

  @Test
  public void canExecuteOneTimeTriggerSuccess() throws Exception {
    final var scheduler = Scheduler.builder()
        .storageConfig(StorageConfig.IN_MEMORY)
        .executionConfig(ExecutionConfig.withMaxConcurrentExecutions(1))
        .build();

    final var zipFilePath = "../resources/example_lambda-1.0-SNAPSHOT.zip";
    final var bytes = Files.readAllBytes(Paths.get(zipFilePath));
    final var name = UUID.randomUUID().toString();

    final var lambdaDefinition = LambdaDefinition.builder()
        .mainClass("org.example.ExampleLambdaSuccess")
        .name(name)
        .zip(bytes)
        .build();

    final var trigger = OneTimeTrigger.builder()
        .jobName(name)
        .executionTime(Instant.now())
        .build();

    scheduler.createLambda(lambdaDefinition);
    scheduler.createTrigger(trigger);
    scheduler.start();

    Awaitility.await()
        .atMost(Duration.ofMillis(500))
        .untilAsserted(() ->
        {
          assertEquals(scheduler.getExecutionStatuses(name).size(), 1);
          assertEquals(scheduler.getExecutionStatuses(name).get(0).state(),
              LambdaExecutionState.SUCCESS);
          assertEquals(scheduler.getExecutionResults(name).size(), 1);
          assertTrue(Map.class.isAssignableFrom(
              scheduler.getExecutionResults(name).get(0).result().getClass()));
          assertTrue(((Map) scheduler.getExecutionResults(name).get(0).result()).get("message")
              .equals("Hello world!"));
        });
  }

  @Test
  public void canExecuteOneTimeTriggerFailure() throws Exception {
    final var scheduler = Scheduler.builder()
        .storageConfig(StorageConfig.IN_MEMORY)
        .executionConfig(ExecutionConfig.withMaxConcurrentExecutions(1))
        .build();
    final var zipFilePath = "../resources/example_lambda-1.0-SNAPSHOT.zip";
    final var bytes = Files.readAllBytes(Paths.get(zipFilePath));
    final var name = UUID.randomUUID().toString();

    final var lambdaDefinition = LambdaDefinition.builder()
        .mainClass("org.example.ExampleLambdaFailure")
        .name(name)
        .zip(bytes)
        .build();

    final var trigger = OneTimeTrigger.builder()
        .jobName(name)
        .executionTime(Instant.now())
        .build();

    scheduler.createLambda(lambdaDefinition);
    scheduler.createTrigger(trigger);
    scheduler.start();

    Awaitility.await()
        .atMost(Duration.ofMillis(500))
        .untilAsserted(() ->
        {
          assertEquals(scheduler.getExecutionStatuses(name).size(), 1);
          assertEquals(scheduler.getExecutionStatuses(name).get(0).state(),
              LambdaExecutionState.FAILED);
          assertEquals(scheduler.getExecutionResults(name).size(), 1);
          assertTrue(RuntimeException.class.isAssignableFrom(
              scheduler.getExecutionResults(name).get(0).result().getClass()));
          assertTrue(
              ((RuntimeException) scheduler.getExecutionResults(name).get(0).result()).getMessage()
                  .equals("Meh."));
        });
  }

}