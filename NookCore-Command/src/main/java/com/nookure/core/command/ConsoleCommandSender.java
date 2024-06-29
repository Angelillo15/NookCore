package com.nookure.core.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nookure.core.CommandSender;
import com.nookure.core.logger.Logger;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class ConsoleCommandSender implements CommandSender {
  @Inject
  private Logger logger;

  @Override
  public void sendMessage(@NotNull Component component) {
    logger.info(component);
  }

  @Override
  public void sendMiniMessage(@NotNull String message, String... placeholders) {
    CommandSender.super.sendMiniMessage(message, placeholders);
  }

  @Override
  public void sendActionbar(@NotNull Component component) {
    // Do nothing
  }

  @Override
  public int getPing() {
    return -1;
  }

  @Override
  public @NotNull Component getDisplayName() {
    return Component.text("Console");
  }

  @Override
  public @NotNull String getName() {
    return "Console";
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return UUID.fromString("00000000-0000-0000-0000-000000000000");
  }

  @Override
  public boolean hasPermission(@NotNull String permission) {
    return true;
  }

  @Override
  public boolean isPlayer() {
    return false;
  }
}