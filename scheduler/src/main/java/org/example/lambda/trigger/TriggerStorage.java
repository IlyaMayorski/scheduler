package org.example.lambda.trigger;

import java.util.List;

public interface TriggerStorage {

  void save(final Trigger trigger);

  List<Trigger> getFiredTriggers();
}
