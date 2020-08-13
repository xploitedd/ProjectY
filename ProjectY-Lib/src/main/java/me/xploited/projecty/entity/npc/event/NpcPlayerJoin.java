package me.xploited.projecty.entity.npc.event;

import me.xploited.projecty.entity.npc.Npc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NpcPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Npc.spawnEveryNpc(e.getPlayer());
    }

}
