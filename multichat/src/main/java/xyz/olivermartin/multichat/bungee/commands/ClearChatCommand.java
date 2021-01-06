package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Clear Chat Command
 * <p>Allows the user to clear their personal chat, the server chat, the global chat, or all servers' chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ClearChatCommand extends Command {

    private final TextComponent EMPTY_LINES;

    public ClearChatCommand() {
        super("mcclearchat", "multichat.chat.clear", ProxyConfigs.ALIASES.getAliases("mcclearchat"));

        // Join space and linebreak character 200 times (= 100 empty lines)
        char space = ' ';
        char lf = '\n';
        char[] output = new char[200];
        // Reverse fori to fill char array properly
        for (int i = 198; i >= 0; i -= 2) {
            output[i] = space;
            output[i + 1] = lf;
        }
        EMPTY_LINES = new TextComponent(new String(output));
    }

    public void execute(CommandSender sender, String[] args) {
        String arg = args.length > 0 ? args[0].toLowerCase() : "self";
        switch (arg) {
            case "self": {
                clearChatSelf(sender);
                break;
            }
            case "all": {
                if (!sender.hasPermission("multichat.chat.clear.all")) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_clearchat_no_permission", "ALL");
                    return;
                }

                clearChatForEveryone(null, null);
                break;
            }
            case "server": {
                if (!(sender instanceof ProxiedPlayer)) {
                    // TODO: Implement message
                    return;
                }

                if (!sender.hasPermission("multichat.chat.clear.server")) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_clearchat_no_permission", "SERVER");
                    return;
                }

                ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
                clearChatForEveryone("command_clearchat_server",
                        target -> proxiedPlayer.getServer().getInfo().equals(target.getServer().getInfo())
                );
                break;
            }
            case "global": {
                if (!sender.hasPermission("multichat.chat.clear.global")) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_clearchat_no_permission", "GLOBAL");
                    return;
                }

                clearChatForEveryone("command_clearchat_global",
                        target -> target.getServer() != null
                                && ProxyConfigs.CONFIG.isGlobalServer(target.getServer().getInfo().getName())
                );
                break;
            }
            default: {
                ProxyConfigs.MESSAGES.sendMessage(sender, "command_clearchat_usage");
                break;
            }
        }
    }

    private void clearChatSelf(CommandSender sender) {
        sender.sendMessage(EMPTY_LINES);
        ProxyConfigs.MESSAGES.sendMessage(sender, "command_clearchat_self");
    }

    private void clearChatForEveryone(String configPath, Predicate<ProxiedPlayer> predicate) {
        Stream<ProxiedPlayer> playerStream = ProxyServer.getInstance().getPlayers().stream();
        if (predicate != null) playerStream = playerStream.filter(predicate);
        playerStream.forEach(target -> {
            target.sendMessage(EMPTY_LINES);
            if (configPath != null && !configPath.isEmpty())
                ProxyConfigs.MESSAGES.sendMessage(target, configPath);
        });
        playerStream.close();
    }
}
