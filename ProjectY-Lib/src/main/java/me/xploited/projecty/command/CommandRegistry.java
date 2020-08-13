package me.xploited.projecty.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandRegistry implements CommandExecutor {

    private final HashMap<String, CommandInfo> handlers = new HashMap<>();
    private final Plugin plugin;

    public CommandRegistry(Plugin plugin) {
       this.plugin = plugin;
    }

    public void registerCommands(CommandHandler handler) {
        try {
            Class<? extends CommandHandler> clazz = handler.getClass();
            Method method = clazz.getDeclaredMethod("handle", CommandSender.class, String[].class);

            Command command = method.getAnnotation(Command.class);
            if (command == null)
                throw new IllegalArgumentException("Invalid command handler!");

            String cmd = command.value();
            PluginCommand pluginCommand = plugin.getServer().getPluginCommand(cmd);
            if (pluginCommand == null)
                throw new IllegalArgumentException("Command must be defined in the plugin.yml!");

            String usage = String.format("/%s %s", cmd, command.usage());
            pluginCommand.setExecutor(this);
            pluginCommand.setUsage(usage);

            Description description = method.getAnnotation(Description.class);
            if (description != null) {
                pluginCommand.setDescription(description.value());
            }

            CommandInfo info = new CommandInfo(handler, command);
            handlers.put(cmd, info);
        } catch (NoSuchMethodException e) {
            // TODO: pass a RuntimeException ???
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        CommandInfo info = handlers.get(command.getName());
        if (info == null)
            return false;

        Command cmd = info.command;
        if (!(sender instanceof Player)) {
            if (cmd.isPlayerRequired()) {
                sender.sendMessage("This command requires a player to be run!");
                return true;
            }
        }

        int argCount = args.length;
        int requiredArgs = cmd.requiredArguments();
        if (requiredArgs > argCount) {
            sender.sendMessage(String.format("%sThis command requires %d arguments but only %d were passed!",
                    ChatColor.RED, requiredArgs, argCount));

            return true;
        }

        info.handler.handle(sender, args);
        return true;
    }

    private static class CommandInfo {

        private final CommandHandler handler;
        private final Command command;
        // TODO: add permission, description, ... attributes

        public CommandInfo(CommandHandler handler, Command command) {
            this.handler = handler;
            this.command = command;
        }

        public CommandHandler getHandler() {
            return handler;
        }

        public Command getCommand() {
            return command;
        }

    }

}
