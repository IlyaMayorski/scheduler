package org.example.lambda.execution.status;

public record LambdaExecutionStatus(
    String name,
    LambdaExecutionState state,
    String executionId
) {

}
