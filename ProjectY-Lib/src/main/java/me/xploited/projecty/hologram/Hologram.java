package me.xploited.projecty.hologram;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;

public class Hologram {

    public static final double LINE_HEIGHT = 0.3;
    private static final Vector LINE_VECTOR = new Vector(0, LINE_HEIGHT, 0);
    private static final LinkedList<Hologram> holograms = new LinkedList<>();

    private final ArrayList<ArmorStand> lines = new ArrayList<>();
    private final World world;
    private final Vector baseLocation;
    private final Vector lastLocation;
    private ArmorStand support;
    private HologramInteractListener listener;

    public Hologram(World world, Vector baseLocation) {
        this.world = world;
        this.baseLocation = baseLocation;
        this.lastLocation = new Vector(0, -0.5, 0)
                .add(baseLocation)
                .add(LINE_VECTOR);

        holograms.add(this);
    }

    public Location getBaseLocation() {
        return baseLocation.toLocation(world);
    }

    public Hologram addLine() {
        return addLine(null);
    }

    public Hologram addLine(@Nullable String line) {
        if (support != null)
            support.remove();

        ArmorStand stand = spawnArmorStand(world, lastLocation);
        if (line != null) {
            stand.setCustomNameVisible(true);
            stand.setCustomName(line);
        }

        lines.add(stand);
        lastLocation.add(LINE_VECTOR);

        support = spawnArmorStand(world, new Vector(0, LINE_HEIGHT, 0)
                .add(lastLocation));

        return this;
    }

    public Hologram setLine(int lineIdx, @NotNull String line) {
        checkLineBounds(lineIdx);
        ArmorStand stand = lines.get(lineIdx);
        stand.setCustomName(line);
        stand.setCustomNameVisible(true);
        return this;
    }

    public Hologram setInteractListener(@Nullable HologramInteractListener listener) {
        this.listener = listener;
        return this;
    }

    public void hologramInteract(int lineIdx) {
        if (listener != null && lineIdx < lines.size())
            listener.onInteract(this, lineIdx);
    }

    public void destroy() {
        for (ArmorStand stand : lines)
            stand.remove();

        if (support != null)
            support.remove();
    }

    @Nullable
    public static Hologram getAssociatedHologram(@NotNull ArmorStand stand) {
        for (Hologram hologram : holograms) {
            if (hologram.support.equals(stand))
                return hologram;

            for (ArmorStand s : hologram.lines) {
                if (s.equals(stand))
                    return hologram;
            }
        }

        return null;
    }

    private void checkLineBounds(int lineIdx) {
        if (lineIdx >= lines.size())
            throw new IndexOutOfBoundsException("Line Index Out Of Bounds!");
    }

    private static ArmorStand spawnArmorStand(World world, Vector vector) {
        ArmorStand stand = (ArmorStand) world.spawnEntity(vector.toLocation(world), EntityType.ARMOR_STAND);
        stand.setSmall(false);
        stand.setInvulnerable(true);
        stand.setVisible(false);
        stand.setGravity(false);

        return stand;
    }

}
