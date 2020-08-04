package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

import java.util.List;
import java.util.Optional;

/**
 * Display Command
 * <p>Displays a message to every player connected to the BungeeCord network</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class DisplayCommand extends Command {
  
    public DisplayCommand() {
        super("mcdisplay", "multichat.staff.display", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("display").toArray(new String[0]));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageManager.sendMessage(sender, "command_display_desc");
            MessageManager.sendMessage(sender, "command_display_usage");
            return;
        }

        displayMessage(String.join(" ", args));
    }

    private void displayMessage(String message) {
        Optional<String> optionalMessage = ChatControl.applyChatRules(message, "display_command", "");
        if (!optionalMessage.isPresent())
            return;

        List<String> noGlobalServers = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG)
                .getConfig().getStringList(ConfigValues.Config.NO_GLOBAL);

        String finalMessage = MultiChatUtil.translateColorCodes(optionalMessage.get());
        ProxyServer.getInstance().getPlayers().stream()
                .filter(target -> target.getServer() != null
                        && !noGlobalServers.contains(target.getServer().getInfo().getName())
                )
                .forEach(target ->
                        target.sendMessage(MultiChat.legacyServers.contains(target.getServer().getInfo().getName())
                                ? ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColorCodes(finalMessage))
                                : ProxyJsonUtils.parseMessage(finalMessage)
                        )
                );

        // Trigger PostBroadcastEvent
        ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("display", message));

        ConsoleManager.logDisplayMessage(message);
    }
}
