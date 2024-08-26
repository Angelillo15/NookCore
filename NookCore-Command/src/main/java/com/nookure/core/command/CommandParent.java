package com.nookure.core.command;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.nookure.core.CommandSender;
import com.nookure.core.annotation.PluginColoredName;
import com.nookure.core.annotation.PluginVersion;
import com.nookure.core.logger.annotation.PluginLoggerColor;
import com.nookure.core.logger.annotation.PluginName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This class represents a command that
 * have sub-commands.
 *
 * <p>
 * This class is abstract and must be extended.
 * The class must be annotated with {@link CommandData}.
 * </p>
 *
 * @see CommandData
 * @since 1.0.0
 */
public abstract class CommandParent extends Command {
  private final Map<String, Command> subCommands = new TreeMap<>();
  private final Map<String, CommandData> subCommandData = new TreeMap<>();
  private final ArrayList<String> subCommandNames = new ArrayList<>();
  @Inject
  private Injector injector;
  @Inject
  @PluginVersion
  private String version;
  @Inject
  @PluginName
  private String pluginName;
  @Inject
  @PluginColoredName
  private String pluginColoredName;
  @PluginLoggerColor
  private NamedTextColor color;


  @Override
  public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
    if (args.isEmpty()) {
      sendHelp(sender, label);
      return;
    }

    Optional<Command> optionalCommand = Optional.ofNullable(subCommands.get(args.get(0)));

    if (optionalCommand.isEmpty()) {
      sendHelp(sender, label);
      return;
    }

    Command command = optionalCommand.get();

    if (!sender.hasPermission(command.getCommandData().permission())) {
      sender.sendMiniMessage(getNoPermissionMessage());
      return;
    }

    command.onCommand(sender, label, args.subList(1, args.size()));
  }

  public void sendHelp(@NotNull CommandSender sender, @NotNull String label) {
    sender.sendMiniMessage(pluginColoredName + " <gray>- <white>v" + version);


    subCommandData.forEach((name, data) -> {
      final Component component = MiniMessage.miniMessage().deserialize(
          String.format(
              "<b>|</b> <white>/%s %s</white> - <gray>%s</gray>",
              label,
              data.name(),
              data.description()
          )
      );

      sender.sendMessage(component);
    });
  }

  @Override
  public void prepare() {
    Arrays.stream(getCommandData().subCommands()).forEach(subCommandClazz -> {
      Command subCommand = injector.getInstance(subCommandClazz);

      subCommands.put(subCommand.getCommandData().name(), subCommand);
      subCommandData.put(subCommand.getCommandData().name(), subCommand.getCommandData());

      if (subCommand.getCommandData().aliases() != null) {
        Arrays.stream(subCommand.getCommandData().aliases()).forEach(alias -> subCommands.put(alias, subCommand));
      }
    });

    HelpCommand helpCommand = new HelpCommand(this);

    subCommands.put("help", helpCommand);
    subCommandData.put("help", helpCommand.getCommandData());

    subCommandNames.addAll(subCommands.keySet());
  }

  @Override
  public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
    if (args.size() == 1) {
      return subCommandNames;
    }

    Optional<Command> optionalCommand = Optional.ofNullable(subCommands.get(args.get(0)));
    return optionalCommand.map(command -> command.onTabComplete(sender, label, args.subList(1, args.size()))).orElseGet(List::of);
  }

  public String getNoPermissionMessage() {
    return "<red>You don't have permission to execute this sub-command";
  }

  @CommandData(
      name = "help",
      description = "Shows the help message"
  )
  private static final class HelpCommand extends Command {
    private final CommandParent parent;

    private HelpCommand(CommandParent parent) {
      this.parent = parent;
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String label, @NotNull List<String> args) {
      parent.sendHelp(sender, label);
    }
  }
}
