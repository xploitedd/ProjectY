package me.xploited.projecty.entity.npc.event;

import me.xploited.projecty.NPCLib;
import me.xploited.projecty.entity.npc.Npc;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class NpcPlayerRespawn implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(NPCLib.getPlugin(), () ->
                Npc.spawnEveryNpc(e.getPlayer()), 20);
    }

}
