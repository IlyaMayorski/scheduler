package org.example.lambda;

public interface LambdaStorage {

  void save(final LambdaRuntimeDefinition lambdaDefinition);

  LambdaRuntimeDefinition getByName(final String name);
}
