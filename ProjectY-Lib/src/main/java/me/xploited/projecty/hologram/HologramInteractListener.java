package me.xploited.projecty.hologram;

@FunctionalInterface
public interface HologramInteractListener {

    void onInteract(Hologram hologram, int lineIdx);

}
