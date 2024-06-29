package com.nookure.core.boot.paper;

import com.nookure.core.boot.Constants;
import com.nookure.core.boot.paper.annotation.Features;
import com.nookure.core.boot.paper.annotation.NookCoreFeatures;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


@SuppressWarnings("UnstableApiUsage")
public abstract class NookCorePluginLoader implements PluginLoader {
  private boolean debug;

  @Override
  public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
    debug = System.getProperty("nookcore.debug") != null;

    MavenLibraryResolver resolver = new MavenLibraryResolver();

    resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());
    resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://maven.nookure.com/").build());
  }

  public void addDependencies(MavenLibraryResolver resolver) {
    Logger logger = Logger.getLogger("NookCore");

    NookCoreFeatures features = getClass().getAnnotation(NookCoreFeatures.class);

    if (features == null) {
      logger.warning("No NookCoreFeatures annotation found on " + getClass().getName());
      return;
    }

    for (Features dependency : features.value()) {
      String full = "com.nookure.core:" + dependency.getArtifactId() + ":" + Constants.VERSION;

      if (debug) {
        logger.info("Adding dependency: " + full);
      }

      resolver.addDependency(new Dependency(new DefaultArtifact(full), null));
    }
  }
}
