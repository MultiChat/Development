package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.ConfigManager;
import xyz.olivermartin.multichat.velocity.Events;
import xyz.olivermartin.multichat.velocity.MessageManager;

public class MultiChatBypassCommand extends Command {

    public MultiChatBypassCommand() {
        super("multichatbypass", ConfigManager.getInstance().getHandler("config.yml").getConfig().getChildrenMap().containsKey("multichatbypasscommand") ? ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("multichatbypasscommand").getList(String::valueOf).toArray(new String[0]) : new String[0]);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.bypass");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (!(sender instanceof Player)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length >= 1) {

            MessageManager.sendMessage(sender, "command_multichatbypass_usage");

        } else {

            if (Events.mcbPlayers.contains(player.getUniqueId())) {

                Events.mcbPlayers.remove(player.getUniqueId());
                MessageManager.sendMessage(sender, "command_multichatbypass_disabled");

            } else {

                Events.mcbPlayers.add(player.getUniqueId());
                MessageManager.sendMessage(sender, "command_multichatbypass_enabled");

            }

        }

    }

}
