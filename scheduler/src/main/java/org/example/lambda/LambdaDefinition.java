package org.example.lambda;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LambdaDefinition {

  final byte[] zip;
  @Getter
  final String mainClass;
  @Getter
  final String name;
}
