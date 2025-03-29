package org.example.lambda.trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryTriggerStorage implements TriggerStorage {

  private final List<Trigger> triggers = Collections.synchronizedList(new ArrayList<>());

  @Override
  public void save(Trigger trigger) {
    triggers.add(trigger);
  }

  @Override
  public List<Trigger> getFiredTriggers() {
    return triggers.stream()
        .filter(Trigger::shouldRun)
        .toList();
  }

}
