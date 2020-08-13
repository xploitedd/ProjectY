package me.xploited.projecty;

import me.xploited.projecty.command.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Test extends JavaPlugin {

    private final Logger log = getLogger();

    @Override
    public void onEnable() {
        log.warning("Test Enabled - DO NOT RUN IN PROD!");

        CommandRegistry registry = new CommandRegistry(this);
        registry.registerCommands(new TestCommand());
    }

    @Override
    public void onDisable() {
        log.info("Test Disabled");
    }


}
