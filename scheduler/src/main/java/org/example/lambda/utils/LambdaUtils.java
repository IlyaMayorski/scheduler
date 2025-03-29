package org.example.lambda.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class LambdaUtils {

  public static byte[] getJarFromLambdaDefinition(byte[] zip) throws IOException {
    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zip))) {
      ZipEntry entry;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        if (entry.getName().endsWith(".jar")) {
          return zipInputStream.readAllBytes();
        }
      }
    }

    throw new RuntimeException("No JAR file found in ZIP.");
  }
}
