package me.xploited.projecty.event;

import me.xploited.projecty.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;

public class PlayerInteractAtEntity implements Listener {

    private final static double MAX_DISTANCE = 2.0;

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            ArmorStand stand = (ArmorStand) e.getRightClicked();
            Hologram hologram = Hologram.getAssociatedHologram(stand);
            if (hologram == null)
                return;

            Location loc = p.getLocation();
            Vector v = hologram.getBaseLocation()
                    .subtract(loc)
                    .toVector();

            double d0 = Math.sqrt(v.getX() * v.getX() + v.getZ() * v.getZ());
            if (d0 > MAX_DISTANCE)
                return;

            Vector entityVector = stand.getLocation().toVector();

            float pitch = loc.getPitch();
            int realPitch = Math.round(pitch);
            double alpha = Math.toRadians(Math.abs(pitch));
            double ha;

            if (realPitch == 0)
                ha = -v.getY();
            else if (realPitch < 0)
                ha = -v.getY() + Math.tan(alpha) * d0;
            else
                ha = -v.getY() - loc.toVector().subtract(entityVector).getY() - Hologram.LINE_HEIGHT;

            int idx = (int) Math.floor(((ha * 10.0) + Hologram.LINE_HEIGHT) / (Hologram.LINE_HEIGHT * 10)) - 1;
            idx = Math.max(idx, 0);

            hologram.hologramInteract(idx);
        }
    }

}
