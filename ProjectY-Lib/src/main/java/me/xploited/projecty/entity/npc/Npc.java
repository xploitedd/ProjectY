package me.xploited.projecty.entity.npc;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.packetwrapper.WrapperPlayServerEntityDestroy;
import com.comphenix.packetwrapper.WrapperPlayServerEntityHeadRotation;
import com.comphenix.packetwrapper.WrapperPlayServerEntityLook;
import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import me.xploited.projecty.NPCLib;
import me.xploited.projecty.hologram.Hologram;
import me.xploited.projecty.task.ScheduledRun;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Npc implements ScheduledRun<Npc> {

    private static final ProtocolManager MANAGER = ProtocolLibrary.getProtocolManager();

    static {
        // register the ENTITY_USE packet listener
        registerUsePacketListener();
    }

    private static final Scoreboard SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();
    private static final HashMap<Integer, Npc> NPC_LIST = new HashMap<>();

    private final Team scoreboardTeam;
    private final EntityPlayer entity;
    private final Hologram hologram;
    private final Location location;
    private GameProfile profile;
    private NpcActionListener listener;

    public Npc(EntityPlayer entity, Location location, GameProfile profile) {
        this.scoreboardTeam = SCOREBOARD.registerNewTeam(entity
                .getUniqueIDString()
                .replaceAll("-", "")
                .substring(0, 16));

        this.entity = entity;
        this.hologram = new Hologram(location.getWorld(), location.toVector());
        this.location = location;
        this.profile = profile;

        scoreboardTeam.addEntry(entity.getName());
        NPC_LIST.put(entity.getId(), this);
    }

    public int getId() {
        return entity.getId();
    }

    public String getName() {
        return entity.getName();
    }

    public Location getLocation() {
        return location;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    public void setNameVisible(boolean nameVisible) {
        scoreboardTeam.setOption(
                Team.Option.NAME_TAG_VISIBILITY,
                nameVisible ? Team.OptionStatus.ALWAYS : Team.OptionStatus.NEVER
        );
    }

    public void sendMessage(Player player, String message, Object... args) {
        String formattedMessage = String.format(message, args);
        player.sendMessage(String.format(
                "%s[%s]: %s%s",
                ChatColor.GOLD,
                getName(),
                ChatColor.RESET,
                formattedMessage
        ));
    }

    public void setActionListener(NpcActionListener listener) {
        this.listener = listener;
    }

    public Npc spawn() {
        for (Player p : Bukkit.getOnlinePlayers())
            spawn(p, this);

        return this;
    }

    public static void spawnEveryNpc(Player p) {
        for (Npc npc : NPC_LIST.values())
            spawn(p, npc);
    }

    public static void removeEveryNpc() {
        for (Npc npc : NPC_LIST.values()) {
            npc.hologram.destroy();
            try {
                npc.scoreboardTeam.unregister();
            } catch (Exception ignored) {
                // this exception is unexpected, but just in case...
            }
        }

        int[] entities = NPC_LIST.values()
                .stream()
                .mapToInt(npc -> npc.entity.getId())
                .toArray();

        AbstractPacket packet = getEntityDestroyPacket(entities);
        for (Player p : Bukkit.getOnlinePlayers())
            packet.sendPacket(p);
    }

    private static void spawn(Player p, Npc npc) {
        // send all the necessary packets
        getPlayerInfoAddPacket(npc).sendPacket(p);
        getNameEntitySpawnPacket(npc).sendPacket(p);
        getEntityMetadataPacket(npc).sendPacket(p);
        Bukkit.getScheduler().runTaskLater(NPCLib.getPlugin(NPCLib.class), () -> {
            getEntityLookPacket(npc).sendPacket(p);
            getEntityHeadRotationPacket(npc).sendPacket(p);
            getPlayerInfoRemovePacket(npc).sendPacket(p);
        }, 20);
    }

    private static AbstractPacket getPlayerInfoAddPacket(Npc npc) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        packet.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        packet.setData(getPlayerInfoData(npc));

        return packet;
    }

    private static AbstractPacket getPlayerInfoRemovePacket(Npc npc) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        packet.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packet.setData(getPlayerInfoData(npc));

        return packet;
    }

    private static List<PlayerInfoData> getPlayerInfoData(Npc npc) {
        return Collections.singletonList(new PlayerInfoData(
                WrappedGameProfile.fromHandle(npc.profile),
                0,
                EnumWrappers.NativeGameMode.ADVENTURE,
                WrappedChatComponent.fromText(npc.getName())
        ));
    }

    private static AbstractPacket getEntityMetadataPacket(Npc npc) {
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(npc.entity.getDataWatcher());
        dataWatcher.setObject(16, WrappedDataWatcher.Registry.get(Byte.class), Byte.MAX_VALUE);
        packet.setEntityID(npc.entity.getId());
        packet.setMetadata(dataWatcher.getWatchableObjects());

        return packet;
    }

    private static AbstractPacket getNameEntitySpawnPacket(Npc npc) {
        Location loc = npc.location;

        WrapperPlayServerNamedEntitySpawn packet = new WrapperPlayServerNamedEntitySpawn();
        packet.setEntityID(npc.entity.getId());
        packet.setPlayerUUID(npc.entity.getUniqueID());
        packet.setPosition(new Vector(loc.getX(), loc.getY(), loc.getZ()));
        packet.setYaw(loc.getYaw());
        packet.setPitch(loc.getPitch());

        return packet;
    }

    private static AbstractPacket getEntityLookPacket(Npc npc) {
        WrapperPlayServerEntityLook packet = new WrapperPlayServerEntityLook();
        packet.setEntityID(npc.entity.getId());

        float yaw = npc.location.getYaw() % 360;
        float bodyYaw = yaw;
        if (yaw < -180 && yaw > -360)
            bodyYaw += 45;
        else if (yaw >= -180 && yaw < 0)
            bodyYaw -= 45;

        packet.setYaw(bodyYaw);
        packet.setPitch(npc.location.getPitch());
        packet.setOnGround(true);

        return packet;
    }

    private static AbstractPacket getEntityHeadRotationPacket(Npc npc) {
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityID(npc.entity.getId());
        packet.setHeadYaw((byte) (npc.location.getYaw() * 256 / 360));

        return packet;
    }

    private static AbstractPacket getEntityDestroyPacket(int[] entities) {
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntityIds(entities);

        return packet;
    }

    private static void registerUsePacketListener() {
        Plugin plugin = NPCLib.getPlugin();
        MANAGER.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (packet.getType() == PacketType.Play.Client.USE_ENTITY) {
                    WrapperPlayClientUseEntity wrapper = new WrapperPlayClientUseEntity(packet);
                    if (wrapper.getType() == EnumWrappers.EntityUseAction.INTERACT_AT)
                        return;

                    Npc npc = NPC_LIST.get(wrapper.getTargetID());
                    if (npc != null && npc.listener != null) {
                        NpcAction action = NpcAction.fromUseAction(wrapper.getType());
                        if (action == NpcAction.OPEN_INVENTORY && packet.getHands() != null) {
                            EnumWrappers.Hand hand = packet.getHands().read(0);
                            if (hand != EnumWrappers.Hand.MAIN_HAND)
                                return;
                        }

                        Player p = event.getPlayer();
                        Bukkit.getScheduler()
                                .runTask(plugin, () -> npc.listener.onNpcAction(npc, action, p));
                    }
                }
            }
        });
    }

}
