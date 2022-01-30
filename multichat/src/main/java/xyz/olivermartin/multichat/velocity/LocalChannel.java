package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

public class LocalChannel extends Channel {

    public LocalChannel() {
        super("local", "", false, false);
    }

    /**
     * This has no purpose as local chat for players is handled by the local servers
     */
    @Override
    public void sendMessage(Player sender, String message, String format) {
        /* EMPTY */
    }

    @Override
    public void sendMessage(String message, CommandSource sender) {

        DebugManager.log("LocalChannel wants to send a cast message!");

        // Use this to relay CASTS to local chat!
        if (sender instanceof Player) {
            BungeeComm.sendChatMessage(message, ((Player) sender).getCurrentServer().get().getServerInfo());
        }

    }

}
