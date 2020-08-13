package me.xploited.projecty.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;

public class SkinProperty extends Property {

    public static final String INTERNAL_PROPERTY_NAME = "textures";

    public SkinProperty(String value, String signature) {
        super(INTERNAL_PROPERTY_NAME, value, signature);
    }

    public void setSkinOfPlayer(Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        profile.setProperty(new ProfileProperty(INTERNAL_PROPERTY_NAME, getValue(), getSignature()));
        player.setPlayerProfile(profile);
    }

    public static SkinProperty skinFromPlayerProfile(PlayerProfile profile) {
        for (ProfileProperty property : profile.getProperties()) {
            if (property.getName().equals(INTERNAL_PROPERTY_NAME))
                return new SkinProperty(property.getValue(), property.getSignature());
        }

        return null;
    }

}
