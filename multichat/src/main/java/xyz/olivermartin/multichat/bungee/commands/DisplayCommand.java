package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.Optional;

/**
 * Display Command
 * <p>Displays a message to every player connected to the BungeeCord network</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class DisplayCommand extends Command {
  
    public DisplayCommand() {
        super("mcdisplay", "multichat.staff.display", ProxyConfigs.ALIASES.getAliases("mcdisplay"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            MessageManager.sendMessage(sender, "command_display_desc");
            MessageManager.sendMessage(sender, "command_display_usage");
            return;
        }

        String message = String.join(" ", args);
        Optional<String> optionalMessage = ChatControl.applyChatRules(sender, message, MessageType.DISPLAY_COMMAND);
        if (!optionalMessage.isPresent())
            return;

        String finalMessage = MultiChatUtil.translateColorCodes(optionalMessage.get());
        ProxyServer.getInstance().getPlayers().stream()
                .filter(target -> target.getServer() != null
                        && ProxyConfigs.CONFIG.isModernServer(target.getServer().getInfo().getName())
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
