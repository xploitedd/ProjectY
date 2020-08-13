package me.xploited.projecty;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.xploited.projecty.command.Command;
import me.xploited.projecty.command.CommandHandler;
import me.xploited.projecty.command.Description;
import me.xploited.projecty.skin.SkinProperty;
import me.xploited.projecty.util.MojangFetcher;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class TestCommand implements CommandHandler {

    @Override
    @Command(value = "hello", isPlayerRequired = true, requiredArguments = 1, usage = "<player>")
    @Description("Hello World Command")
    public void handle(CommandSender sender, String[] arguments) {
        Player p = (Player) sender;
        String playerName = arguments[0];

        try {
            PlayerProfile profile = MojangFetcher.fetchPlayerProfile(playerName);
            SkinProperty property = SkinProperty.skinFromPlayerProfile(profile);
            if (property != null)
                property.setSkinOfPlayer(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
