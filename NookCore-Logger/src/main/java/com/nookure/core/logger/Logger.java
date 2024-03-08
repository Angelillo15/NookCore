package com.nookure.core.logger;

import com.google.inject.Inject;
import com.nookure.core.annotation.PluginDebug;
import com.nookure.core.logger.annotation.PluginAudience;
import com.nookure.core.logger.annotation.PluginLoggerColor;
import com.nookure.core.logger.annotation.PluginName;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public class Logger {
  @Inject
  @PluginLoggerColor
  private NamedTextColor color;

  @Inject
  @PluginName
  private String pluginName;

  @Inject
  @PluginAudience
  private Audience console;

  @Inject
  @PluginDebug
  private AtomicBoolean debug;

  /**
   * Log a message to the console
   *
   * @param message The message to log
   */
  void info(String message) {
    info(MiniMessage.miniMessage().deserialize("<white>" + message));
  }

  /**
   * Log a warning to the console
   *
   * @param message The message to log
   */
  void warning(String message) {
    warning(Component.text(message).color(NamedTextColor.YELLOW));
  }

  /**
   * Log a severe error to the console
   *
   * @param message The message to log
   */
  void severe(String message) {
    severe(Component.text(message).color(NamedTextColor.RED));
  }

  /**
   * Log a debug message to the console
   *
   * @param message The message to log
   */
  void debug(String message) {
    debug(Component.text(message).color(NamedTextColor.GRAY));
  }

  void info(String message, Object... args) {
    info(String.format(message, args));
  }

  void warning(String message, Object... args) {
    warning(String.format(message, args));
  }

  void severe(String message, Object... args) {
    severe(String.format(message, args));
  }

  void debug(String message, Object... args) {
    debug(String.format(message, args));
  }

  /**
   * Log a message to the console
   *
   * @param component The component to log
   * @see net.kyori.adventure.text.Component
   */
  public void info(Component component) {
    console.sendMessage(getDefaultStyle(component, NamedTextColor.GRAY, "INFO"));
  }

  /**
   * Log a warning to the console
   *
   * @param component The component to log
   * @see net.kyori.adventure.text.Component
   */
  public void warning(Component component) {
    console.sendMessage(getDefaultStyle(component, NamedTextColor.YELLOW, "WARNING"));
  }

  /**
   * Log a severe error to the console
   *
   * @param component The component to log
   * @see net.kyori.adventure.text.Component
   */
  public void severe(Component component) {
    console.sendMessage(getDefaultStyle(component, NamedTextColor.RED, "ERROR"));
  }

  /**
   * Log a debug message to the console
   *
   * @param component The component to log
   * @see net.kyori.adventure.text.Component
   */
  public void debug(Component component) {
    if (debug.get()) {
      console.sendMessage(getDefaultStyle(component, NamedTextColor.GRAY, "DEBUG"));
    }
  }

  private Component getDefaultStyle(Component component, NamedTextColor color, String mode) {
    return Component
        .text(pluginName)
        .color(color)
        .append(Component.text(" | ").color(color))
        .append(Component.text(mode).color(color))
        .append(Component.text(" > ").color(color))
        .append(component);
  }
}
