package me.xploited.projecty.entity.npc;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface NpcActionListener {

    void onNpcAction(Npc npc, NpcAction action, Player p);

}
