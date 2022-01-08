package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.olivermartin.multichat.velocity.BungeeComm;
import xyz.olivermartin.multichat.velocity.ConfigManager;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;

import java.util.regex.PatternSyntaxException;

/**
 * Execute Command
 * <p>Used to execute commands remotely on Spigot servers</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MultiChatExecuteCommand extends Command {

    public MultiChatExecuteCommand() {
        super("multichatexecute", ConfigManager.getInstance().getHandler("config.yml").getConfig().getChildrenMap().containsKey("multichatexecutecommand") ? ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("multichatexecutecommand").getList(String::valueOf).toArray(new String[0]) : new String[]{"mcexecute", "mce", "gexecute", "gexe", "gcommand"});
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.execute");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            MessageManager.sendMessage(sender, "command_execute_usage");

        } else {

            String server = ".*";
            boolean playerFlag = false;
            String player = ".*";

            // Handle flags
            int index = 0;

            while (index < args.length) {

                if (args[index].equalsIgnoreCase("-s")) {
                    if (index + 1 < args.length) {
                        server = args[index + 1];
                    }
                } else if (args[index].equalsIgnoreCase("-p")) {
                    if (index + 1 < args.length) {
                        playerFlag = true;
                        player = args[index + 1];
                    }
                } else {
                    break;
                }

                index = index + 2;

            }


            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                if (index > 0) {
                    index--;
                } else {
                    message.append(arg).append(" ");
                }
            }

            message = new StringBuilder(message.toString().trim());

            try {

                for (RegisteredServer ser : MultiChat.getInstance().getServer().getAllServers()) {
                    var s = ser.getServerInfo();
                    if (s.getName().matches(server)) {

                        if (playerFlag) {
                            for (Player p : MultiChat.getInstance().getServer().getAllPlayers()) {
                                if (p.getUsername().matches(player)) {
                                    BungeeComm.sendPlayerCommandMessage(message.toString(), p.getUsername(), s);
                                }
                            }
                        } else {
                            BungeeComm.sendCommandMessage(message.toString(), s);
                        }
                    }

                }

                MessageManager.sendMessage(sender, "command_execute_sent");

            } catch (PatternSyntaxException e) {

                MessageManager.sendMessage(sender, "command_execute_regex");

            }

        }
    }

}
