package com.nookure.core.command.paper;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.nookure.core.PlayerWrapperBase;
import com.nookure.core.command.Command;
import com.nookure.core.command.CommandManager;
import com.nookure.core.command.config.CommandConfig;
import com.nookure.core.command.config.CommandPartial;
import com.nookure.core.config.ConfigurationContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
public class PaperCommandManager<P extends PlayerWrapperBase> extends CommandManager {
  private static final CommandMap commandMap;

  static {
    try {
      commandMap = (CommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
    } catch (Exception e) {
      throw new RuntimeException("Failed to get command map");
    }
  }

  @Inject
  private Injector injector;
  @Inject
  private ConfigurationContainer<CommandConfig> commandConfig;

  @Override
  public void registerCommand(@NotNull Command command) {
    TemplateCommand<P> templateCommand = new TemplateCommand(command);
    injector.injectMembers(templateCommand);
    CommandPartial commandPartial;

    if (commandConfig.get().getCommands().containsKey("example")) {
      commandConfig.get().getCommands().remove("example");
      commandConfig.save().join();
    }

    if (!commandConfig.get().getCommands().containsKey(command.getCommandData().name())) {
      commandPartial = new CommandPartial(
          command.getCommandData().name(),
          List.of(command.getCommandData().aliases()),
          command.getCommandData().permission(),
          command.getCommandData().description(),
          command.getCommandData().usage(),
          true
      );

      commandConfig.get().getCommands().put(command.getCommandData().name(), commandPartial);
      commandConfig.save().join();
    } else {
      commandPartial = commandConfig.get().getCommands().get(command.getCommandData().name());
    }

    templateCommand.setName(commandPartial.name());

    if (commandPartial.aliases() != null && !commandPartial.aliases().isEmpty())
      templateCommand.setAliases(commandPartial.aliases());
    if (commandPartial.permission() != null && !commandPartial.permission().isEmpty())
      templateCommand.setPermission(commandPartial.permission());
    if (commandPartial.description() != null && !commandPartial.description().isEmpty())
      templateCommand.setDescription(commandPartial.description());
    if (commandPartial.usage() != null && !commandPartial.usage().isEmpty())
      templateCommand.setUsage(commandPartial.usage());

    commandMap.register("nkstaff", templateCommand);
    command.prepare();
  }

  @Override
  public void unregisterCommand(@NotNull Command command) {
    org.bukkit.command.Command cmd = commandMap.getCommand(command.getCommandData().name());

    if (cmd != null) {
      cmd.unregister(commandMap);
    }
  }
}
