package org.example.lambda.executor;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.example.lambda.LambdaRuntimeDefinition;

public class LambdaExecutionContextCacheProvider {

  final Map<LambdaRuntimeDefinition, Supplier> loadedContexts = new ConcurrentHashMap<>();

  public Supplier prepareExecutionContext(final LambdaRuntimeDefinition lambdaRuntimeDefinition) {
    if (!loadedContexts.containsKey(lambdaRuntimeDefinition)) {
      try {
        final var path = Files.createTempFile("RemoteClassLoader", "jar");
        path.toFile().deleteOnExit();
        final var is = new ByteArrayInputStream(lambdaRuntimeDefinition.getJar());
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);

        final var classLoader = new URLClassLoader(new URL[]{path.toUri().toURL()},
            getClass().getClassLoader());
        final var loadedClass = classLoader.loadClass(lambdaRuntimeDefinition.getMainClass());
        final var instance = loadedClass.getDeclaredConstructor().newInstance();

        if (!(instance instanceof Supplier<?>)) {
          throw new RuntimeException("Entry point does not implement Supplier");
        }

        loadedContexts.put(lambdaRuntimeDefinition, (Supplier) instance);

      } catch (Exception e) {
        throw new RuntimeException("Failed to prepare execution context", e);
      }
    }
    return loadedContexts.get(lambdaRuntimeDefinition);
  }
}
