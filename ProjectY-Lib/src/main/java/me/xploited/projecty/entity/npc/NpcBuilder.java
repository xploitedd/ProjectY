package me.xploited.projecty.entity.npc;

import com.mojang.authlib.GameProfile;
import me.xploited.projecty.skin.SkinProperty;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

import java.util.UUID;

public class NpcBuilder {

    private static int nameCounter;

    private String name;
    private SkinProperty skin;
    private boolean nameVisible;

    public NpcBuilder setName(String name) {
        this.name = ChatColor.stripColor(name);
        return this;
    }

    public NpcBuilder setSkin(SkinProperty skin) {
        this.skin = skin;
        return this;
    }

    public NpcBuilder setNameVisible(boolean nameVisible) {
        this.nameVisible = nameVisible;
        return this;
    }

    private String getName() {
        return name == null ? "NPC_" + nameCounter++ : name;
    }

    public Npc build(Location loc) {
        UUID uuid = UUID.randomUUID();
        World world = loc.getWorld();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        GameProfile gp = new GameProfile(uuid, getName());
        if (skin != null)
            gp.getProperties().put(SkinProperty.INTERNAL_PROPERTY_NAME, skin);

        EntityPlayer entity = new EntityPlayer(server, nmsWorld, gp, new PlayerInteractManager(nmsWorld));

        double x = loc.getBlockX() + 0.5;
        double y = world.getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()) + 1;
        double z = loc.getBlockZ() + 0.5;
        float yaw = Math.round(loc.getYaw() / 45 % 8) * 45;

        entity.setLocation(x, y, z, yaw, 0f);

        Npc npc = new Npc(entity, new Location(world, x, y, z, yaw, 0f), gp);
        npc.setNameVisible(nameVisible);

        return npc;
    }

}
