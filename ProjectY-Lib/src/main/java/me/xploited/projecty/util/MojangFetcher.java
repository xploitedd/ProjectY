package me.xploited.projecty.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MojangFetcher {

    private static final JsonParser PARSER = new JsonParser();
    private static final String PLAYER_TO_UUID_ENDPOINT = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String PLAYER_PROFILE_ENDPOINT = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    @NotNull
    public static UUID fetchUniqueIdOf(@NotNull String playerName) throws IOException {
        return getFromOnlinePlayerOrElse(playerName, Entity::getUniqueId, () -> {
            URL request = new URL(String.format(PLAYER_TO_UUID_ENDPOINT, playerName));
            HttpURLConnection conn = (HttpURLConnection) request.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IllegalArgumentException("Invalid player name");

            try (Reader reader = new InputStreamReader(conn.getInputStream())) {
                JsonObject obj = PARSER.parse(reader).getAsJsonObject();
                String uuidStr = obj.get("id").getAsString();
                return UUID.fromString(getUuidWithDashes(uuidStr));
            }
        });
    }

    @NotNull
    public static PlayerProfile fetchPlayerProfile(@NotNull String playerName) throws IOException {
        return getFromOnlinePlayerOrElse(playerName, Player::getPlayerProfile, () -> {
            UUID uuid = fetchUniqueIdOf(playerName);
            PlayerProfile profile = Bukkit.createProfile(uuid, playerName);

            URL request = new URL(String.format(PLAYER_PROFILE_ENDPOINT, getUuidWithoutDashes(uuid)));
            HttpURLConnection conn = (HttpURLConnection) request.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IllegalArgumentException("Invalid player uuid");

            try (Reader reader = new InputStreamReader(conn.getInputStream())) {
                Spliterator<JsonElement> spliterator = PARSER.parse(reader)
                        .getAsJsonObject()
                        .get("properties")
                        .getAsJsonArray()
                        .spliterator();

                Collection<ProfileProperty> properties = StreamSupport.stream(spliterator, false)
                        .map(el -> {
                            JsonObject obj = el.getAsJsonObject();
                            String name = obj.get("name").getAsString();
                            String value = obj.get("value").getAsString();
                            JsonElement signature = obj.get("signature");

                            if (signature == null)
                                return new ProfileProperty(name, value);
                            else
                                return new ProfileProperty(name, value, signature.getAsString());
                        }).collect(Collectors.toList());

                profile.setProperties(properties);
                return profile;
            }
        });
    }

    public static String getUuidWithoutDashes(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    public static String getUuidWithDashes(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, '-').insert(13, '-').insert(18, '-').insert(23, '-');
        return sb.toString();
    }

    private static <T> T getFromOnlinePlayerOrElse(String player, Function<Player, T> func, SupplierIO<T> supplier)
            throws IOException {

        Player p = Bukkit.getPlayer(player);
        if (p != null)
            return func.apply(p);

        return supplier.get();
    }

}
