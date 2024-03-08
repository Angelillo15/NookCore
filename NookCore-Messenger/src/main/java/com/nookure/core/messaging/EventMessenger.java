package com.nookure.core.messaging;

import com.google.inject.Inject;
import com.nookure.core.PlayerWrapperBase;
import com.nookure.core.annotation.PluginDebug;
import com.nookure.core.event.Event;
import com.nookure.core.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class EventMessenger implements AutoCloseable {
  @Inject
  private Logger logger;
  @Inject
  @PluginDebug
  private AtomicBoolean debug;

  /**
   * Prepares the event transport for use.
   */
  public abstract void prepare();

  /**
   * Publishes an event to the event bus.
   *
   * @param sender The sender of the event
   * @param data   The event data
   */
  public abstract void publish(@NotNull PlayerWrapperBase sender, byte @NotNull [] data);

  public void publish(@NotNull PlayerWrapperBase sender, @NotNull Event event) {
    Objects.requireNonNull(sender);
    Objects.requireNonNull(event);

    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
      objectOutputStream.writeObject(event);
      objectOutputStream.flush();


      publish(sender, byteArrayOutputStream.toByteArray());
    } catch (IOException e) {
      logger.severe("Error while serializing event");
      if (debug.get()) {
        throw new RuntimeException(e);
      }
    }
  }

  @NotNull
  public Optional<Event> decodeEvent(byte @NotNull [] message) {
    Objects.requireNonNull(message);

    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(message));
      return Optional.of((Event) objectInputStream.readObject());
    } catch (Exception e) {
      logger.severe("Error while decoding event from object stream");
      if (debug.get()) {
        throw new RuntimeException(e);
      }
    }

    return Optional.empty();
  }

  @Override
  public void close() throws Exception {
  }
}
