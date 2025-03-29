package org.example;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.time.Instant;
import org.example.config.ExecutionConfig;
import org.example.config.StorageConfig;
import org.example.lambda.LambdaDefinition;
import org.example.lambda.trigger.CronTrigger;
import org.example.lambda.trigger.OneTimeTrigger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/lambda")
class LambdaController {

  private final Scheduler scheduler = Scheduler.builder()
      .storageConfig(StorageConfig.IN_MEMORY)
      .executionConfig(ExecutionConfig.withMaxConcurrentExecutions(10))
      .build();

  @PostConstruct
  public void init() {
    scheduler.start();
  }

  @PreDestroy
  public void destroy() {
    scheduler.stop();
  }

  @PostMapping("/{lambdaName}/cron")
  public ResponseEntity<Void> triggerCron(@PathVariable("lambdaName") String name,
      @RequestBody String cron) {
    final var trigger = CronTrigger.builder()
        .jobName(name)
        .cronExpression(cron)
        .build();

    scheduler.createTrigger(trigger);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{lambdaName}/trigger")
  public ResponseEntity<Void> triggerOneTime(@PathVariable("lambdaName") String name) {
    final var trigger = OneTimeTrigger.builder()
        .jobName(name)
        .executionTime(Instant.now())
        .build();

    scheduler.createTrigger(trigger);
    return ResponseEntity.ok().build();
  }

  @PostMapping
  public ResponseEntity<String> createLambda(
      @RequestParam("file") MultipartFile file,
      @RequestParam("name") String name,
      @RequestParam("handler") String handler
  ) throws IOException {
    scheduler.createLambda(
        LambdaDefinition.builder()
            .zip(file.getBytes())
            .mainClass(handler)
            .name(name)
            .build());
    return ResponseEntity.ok("Lambda created successfully.");
  }

  @GetMapping("/{lambdaName}/execution")
  public ResponseEntity<Object> getLambdaExecution(@PathVariable String lambdaName) {
    return ResponseEntity.ok(scheduler.getExecutionStatuses(lambdaName));
  }

  @GetMapping("/{lambdaName}/results")
  public ResponseEntity<Object> getLambdaResults(@PathVariable String lambdaName) {
    return ResponseEntity.ok(scheduler.getExecutionResults(lambdaName));
  }
}