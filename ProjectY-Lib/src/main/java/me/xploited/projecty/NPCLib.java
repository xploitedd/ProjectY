package me.xploited.projecty;

import me.xploited.projecty.entity.npc.event.NpcPlayerRespawn;
import me.xploited.projecty.entity.npc.event.NpcPlayerJoin;
import me.xploited.projecty.entity.npc.event.NpcPluginDisable;
import me.xploited.projecty.event.PlayerInteractAtEntity;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class NPCLib extends JavaPlugin {

    private final @NotNull Logger log = getLogger();

    @Override
    public void onEnable() {
        log.info("Enabled!");

        Bukkit.getServer().getPluginManager().registerEvents(new NpcPlayerJoin(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NpcPluginDisable(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new NpcPlayerRespawn(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractAtEntity(), this);
    }

    @Override
    public void onDisable() {
        log.info("Disabled!");
    }

    public static Plugin getPlugin() {
        return getPlugin(NPCLib.class);
    }

}
