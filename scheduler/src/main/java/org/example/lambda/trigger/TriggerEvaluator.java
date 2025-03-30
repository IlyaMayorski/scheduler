package org.example.lambda.trigger;

import java.util.concurrent.atomic.AtomicBoolean;
import org.example.lambda.executor.LambdaExecutionQueue;

public class TriggerEvaluator extends Thread {

  private final AtomicBoolean running = new AtomicBoolean(false);
  private final TriggerStorage triggerStorage;
  private final LambdaExecutionQueue lambdaExecutionQueue;

  @lombok.Builder
  private TriggerEvaluator(final TriggerStorage triggerStorage,
      final
      LambdaExecutionQueue lambdaExecutionQueue) {
    this.triggerStorage = triggerStorage;
    this.lambdaExecutionQueue = lambdaExecutionQueue;
  }

  @Override
  public void start() {
    System.out.println("Starting TriggerEvaluator main thread");
    running.set(true);
    super.start();
  }

  public void shutdown() {
    System.out.println("Shutting down TriggerEvaluator main thread");
    running.set(false);
  }

  @Override
  public void run() {
    while (running.get()) {
      final var triggers = this.triggerStorage.getFiredTriggers();
      triggers.forEach(trigger -> {
        lambdaExecutionQueue.add(trigger.getJobName());
        trigger.updateNextExecution();
      });
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
