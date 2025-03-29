package org.example.lambda.trigger;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import java.time.Instant;
import java.time.ZoneId;
import lombok.Builder;

public class CronTrigger implements Trigger {

  private final String jobName;
  private final ExecutionTime executionTime;
  private Instant nextExecution;

  @Builder
  private CronTrigger(final String jobName,
      final String cronExpression) {
    this.jobName = jobName;

    //Unit cron supports only mins, Spring supports seconds aswell
    final var cronParser = new CronParser(
        CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING));
    final var cron = cronParser.parse(cronExpression);
    cron.validate();

    this.executionTime = ExecutionTime.forCron(cron);
    this.nextExecution = computeNextExecution();
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  @Override
  public boolean shouldRun() {
    return Instant.now().isAfter(nextExecution);
  }

  @Override
  public void updateNextExecution() {
    this.nextExecution = computeNextExecution();
  }

  private Instant computeNextExecution() {
    return executionTime
        .nextExecution(Instant.now().atZone(ZoneId.systemDefault()))
        .map(zonedDateTime -> zonedDateTime.toInstant())
        .orElse(Instant.MAX);
  }
}
