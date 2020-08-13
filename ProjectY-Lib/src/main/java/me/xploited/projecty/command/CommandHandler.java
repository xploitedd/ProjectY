package me.xploited.projecty.command;

import org.bukkit.command.CommandSender;

public interface CommandHandler {

    void handle(CommandSender sender, String[] arguments);

}
