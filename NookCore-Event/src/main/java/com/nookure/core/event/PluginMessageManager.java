package com.nookure.core.event;

import com.google.inject.Inject;
import com.nookure.core.annotation.PluginDebug;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public abstract class PluginMessageManager<P> {
  @Inject
  private Logger logger;
  @Inject
  @PluginDebug
  private AtomicBoolean debug;
  abstract public void sendEvent(@NotNull Event event, @NotNull P player);

  @NotNull
  public Optional<Event> decodeEvent(@NotNull ObjectInputStream objetStream) {
    Objects.requireNonNull(objetStream);

    try {
      return Optional.of((Event) objetStream.readObject());
    } catch (IOException | ClassNotFoundException e) {
      logger.severe("Error while decoding event from object stream");
      if (debug.get()) {
        throw new RuntimeException(e);
      }
    }

    return Optional.empty();
  }
}
