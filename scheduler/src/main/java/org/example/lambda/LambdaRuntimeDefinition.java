package org.example.lambda;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class LambdaRuntimeDefinition {

  final byte[] jar;
  @Getter
  final String mainClass;
  @Getter
  final String name;
}
