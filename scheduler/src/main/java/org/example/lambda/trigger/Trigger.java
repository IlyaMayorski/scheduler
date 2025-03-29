package org.example.lambda.trigger;

public interface Trigger {

  String getJobName();

  boolean shouldRun();

  void updateNextExecution();
}
