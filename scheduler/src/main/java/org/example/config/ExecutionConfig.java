package org.example.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExecutionConfig {

  final private Integer maxConcurrentExecutions;

  public static ExecutionConfig withMaxConcurrentExecutions(final Integer maxConcurrentExecutions) {
    return ExecutionConfig.builder().maxConcurrentExecutions(maxConcurrentExecutions).build();
  }
}
