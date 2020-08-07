package xyz.olivermartin.multichat.bungee.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

/**
 * Execute Command
 * <p>Used to execute commands remotely on Spigot servers</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MultiChatExecuteCommand extends Command {

    public MultiChatExecuteCommand() {
        super("mcexecute", "multichat.execute", ProxyConfigs.ALIASES.getAliases("mcexecute"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            MessageManager.sendMessage(sender, "command_execute_usage");
            return;
        }

        List<String> arguments = new ArrayList<>(Arrays.asList(args));

        String server = ".*";
        int serverArgIndex = arguments.indexOf("-s");
        if (serverArgIndex > -1) {
            server = arguments.get(serverArgIndex + 1);
            arguments.remove("-s");
            arguments.remove(server);
        }

        String player = ".*";
        int playerArgIndex = arguments.indexOf("-p");
        boolean playerFlag = playerArgIndex > -1;
        if (playerFlag) {
            player = arguments.get(playerArgIndex + 1);
            arguments.remove("-p");
            arguments.remove(player);
        }

        String message = String.join(" ", arguments);

        Pattern serverPattern;
        Pattern playerPattern;

        try {
            serverPattern = Pattern.compile(server);
            playerPattern = Pattern.compile(player);
        } catch (PatternSyntaxException ex) {
            MessageManager.sendMessage(sender, "command_execute_regex");
            return;
        }

        ProxyServer.getInstance().getServers().values().stream()
                .filter(serverInfo -> serverPattern.matcher(serverInfo.getName()).matches())
                .forEach(serverInfo -> {
                    if (!playerFlag) {
                        ProxyLocalCommunicationManager.sendCommandMessage(message, serverInfo);
                        return;
                    }

                    serverInfo.getPlayers().stream()
                            .filter(target -> playerPattern.matcher(target.getName()).matches())
                            .forEach(target ->
                                    ProxyLocalCommunicationManager.sendPlayerCommandMessage(message,
                                            target.getName(),
                                            serverInfo
                                    )
                            );
                });
    }
}
