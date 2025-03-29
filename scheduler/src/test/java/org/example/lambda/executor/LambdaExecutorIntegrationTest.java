package org.example.lambda.executor;

import java.time.Duration;
import java.util.UUID;
import org.awaitility.Awaitility;
import org.example.config.ExecutionConfig;
import org.example.lambda.LambdaRuntimeDefinition;
import org.example.lambda.execution.result.LambdaExecutionResultStorage;
import org.example.lambda.execution.status.LambdaExecutionState;
import org.example.lambda.execution.status.LambdaStatusStorage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LambdaExecutorIntegrationTest {
  final LambdaExecutionContextCacheProvider contextProviderMock =
      Mockito.mock(LambdaExecutionContextCacheProvider.class);
  final LambdaStatusStorage lambdaStatusStorageMock =
      Mockito.mock(LambdaStatusStorage.class);
  final LambdaExecutionResultStorage lambdaExecutionResultStorageMock =
      Mockito.mock(LambdaExecutionResultStorage.class);

  final LambdaExecutor sut = LambdaExecutor.builder()
      .lambdaStatusStorage(lambdaStatusStorageMock)
      .executionResultStorage(lambdaExecutionResultStorageMock)
      .executionContextCacheProvider(contextProviderMock)
      .executionConfig(ExecutionConfig.withMaxConcurrentExecutions(1))
      .build();

  @Test
  public void logsExecutionStatusSuccess() {
    final var result = UUID.randomUUID().toString();
    final var name = UUID.randomUUID().toString();

    Mockito.when(contextProviderMock.prepareExecutionContext(Mockito.any()))
        .thenReturn(() -> result);

    sut.executeLambda(LambdaRuntimeDefinition.builder()
        .name(name)
        .build());

    Awaitility.await()
        .atMost(Duration.ofMillis(101))
        .untilAsserted(() ->
        {
          Mockito.verify(lambdaStatusStorageMock, Mockito.times(2))
              .upsert(Mockito.argThat(argument -> argument.name().equals(name)));
          Mockito.verify(lambdaStatusStorageMock, Mockito.times(1))
              .upsert(Mockito.argThat(argument -> argument.state().equals(LambdaExecutionState.RUNNING)));
          Mockito.verify(lambdaStatusStorageMock, Mockito.times(1))
              .upsert(Mockito.argThat(argument -> argument.state().equals(LambdaExecutionState.SUCCESS)));
        });

  }

  @Test
  public void logsExecutionStatusException() {
    final var result = new RuntimeException(UUID.randomUUID().toString());
    final var name = UUID.randomUUID().toString();

    Mockito.when(contextProviderMock.prepareExecutionContext(Mockito.any()))
        .thenReturn(() -> {
          throw result;
        });

    sut.executeLambda(LambdaRuntimeDefinition.builder()
        .name(name)
        .build());

    Awaitility.await()
        .atMost(Duration.ofMillis(101))
        .untilAsserted(() ->
        {
          Mockito.verify(lambdaStatusStorageMock, Mockito.times(2))
              .upsert(Mockito.argThat(argument -> argument.name().equals(name)));
          Mockito.verify(lambdaStatusStorageMock, Mockito.times(1))
              .upsert(Mockito.argThat(argument -> argument.state().equals(LambdaExecutionState.RUNNING)));
          Mockito.verify(lambdaStatusStorageMock, Mockito.times(1))
              .upsert(Mockito.argThat(argument -> argument.state().equals(LambdaExecutionState.FAILED)));
        });

  }

  @Test
  public void logsExecutionResultSuccess() {
    final var result = UUID.randomUUID().toString();
    final var name = UUID.randomUUID().toString();

    Mockito.when(contextProviderMock.prepareExecutionContext(Mockito.any()))
        .thenReturn(() -> result);

    sut.executeLambda(LambdaRuntimeDefinition.builder()
            .name(name)
        .build());

    Awaitility.await()
        .atMost(Duration.ofMillis(101))
        .untilAsserted(() ->
            Mockito.verify(lambdaExecutionResultStorageMock, Mockito.times(1))
                .save(Mockito.argThat(argument -> argument.result().equals(result))));
  }

  @Test
  public void logsExecutionResultException() {
    final var result = new RuntimeException(UUID.randomUUID().toString());
    final var name = UUID.randomUUID().toString();

    Mockito.when(contextProviderMock.prepareExecutionContext(Mockito.any()))
        .thenReturn(() -> {
          throw result;
        });

    sut.executeLambda(LambdaRuntimeDefinition.builder()
        .name(name)
        .build());

    Awaitility.await()
        .atMost(Duration.ofMillis(101))
        .untilAsserted(() ->
            Mockito.verify(lambdaExecutionResultStorageMock, Mockito.times(1))
                .save(Mockito.argThat(argument -> argument.result().equals(result))));
  }

}
