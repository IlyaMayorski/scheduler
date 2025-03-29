package org.example.lambda.trigger;

import java.time.Instant;
import lombok.Builder;

public class OneTimeTrigger implements Trigger {

  private final String jobName;
  private final Instant executionTime;
  private boolean executed = false;

  @Builder
  private OneTimeTrigger(final String jobName,
      final Instant executionTime) {
    this.jobName = jobName;
    this.executionTime = executionTime;
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  @Override
  public boolean shouldRun() {
    return !executed && Instant.now().isAfter(executionTime);
  }

  @Override
  public void updateNextExecution() {
    executed = true;
  }
}
