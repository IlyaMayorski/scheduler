package org.example.lambda.execution.result;

public record LambdaExecutionResult(
    String name,
    String executionId,
    Object result
) {

}
