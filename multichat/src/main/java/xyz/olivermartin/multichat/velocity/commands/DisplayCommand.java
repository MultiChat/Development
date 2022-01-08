package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.velocity.*;
import xyz.olivermartin.multichat.velocity.events.PostBroadcastEvent;

/**
 * Display Command
 * <p>Displays a message to every player connected to the BungeeCord network</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class DisplayCommand extends Command {

    private static final String[] aliases = new String[]{};

    public DisplayCommand() {
        super("display", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.staff.display");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            MessageManager.sendMessage(sender, "command_display_desc");
            MessageManager.sendMessage(sender, "command_display_usage");

        } else {

            String message = MultiChatUtil.getMessageFromArgs(args);

            displayMessage(message);
        }
    }

    public static void displayMessage(String message) {
        message = ChatControl.applyChatRules(message, "display_command", "").get();
        message = MultiChatUtil.reformatRGB(message);
        ConfigurationNode config = ConfigManager.getInstance().getHandler("config.yml").getConfig();

        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
            if (onlineplayer.getCurrentServer().isPresent()) {
                if (!config.getNode("no_global").getList(String::valueOf).contains(
                        onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                    if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName()))
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                    else
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                }
            }
        }

        // Trigger PostBroadcastEvent
        MultiChat.getInstance().getServer().getEventManager().fire(new PostBroadcastEvent("display", message));

        ConsoleManager.logDisplayMessage(message);
    }
}
