package com.nookure.core.command.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;

@ConfigSerializable
public class CommandConfig {
  private static final HashMap<String, CommandPartial> DEFAULT;

  static {
    DEFAULT = new HashMap<>();

    DEFAULT.put("example", new CommandPartial(
        "example",
        List.of(),
        "example.permission",
        "Example command",
        "An example command",
        true
    ));
  }

  @Setting
  private HashMap<String, CommandPartial> commands = new HashMap<>(DEFAULT);

  public HashMap<String, CommandPartial> getCommands() {
    return commands;
  }

  public void addCommand(String name, CommandPartial command) {
    commands.put(name, command);
  }
}