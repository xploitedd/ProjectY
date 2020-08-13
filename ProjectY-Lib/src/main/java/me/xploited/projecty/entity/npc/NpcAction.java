package me.xploited.projecty.entity.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;

public enum NpcAction {

    ATTACK,
    OPEN_INVENTORY;

    public static NpcAction fromUseAction(EnumWrappers.EntityUseAction action) {
        switch (action) {
            case ATTACK:
                return ATTACK;
            case INTERACT:
                return OPEN_INVENTORY;
            default:
                return null;
        }
    }

}
