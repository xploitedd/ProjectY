package me.xploited.projecty.entity.npc.event;

import me.xploited.projecty.entity.npc.Npc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

public class NpcPluginDisable implements Listener {

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        Npc.removeEveryNpc();
    }

}
