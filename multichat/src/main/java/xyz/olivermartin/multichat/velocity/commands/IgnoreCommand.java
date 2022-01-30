package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.*;

public class IgnoreCommand extends Command {

    public IgnoreCommand() {
        super("ignore", ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getNode("ignorecommand").getList(String::valueOf).toArray(new String[0]));
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.ignore");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length != 1) {
            MessageManager.sendMessage(sender, "ignore_usage");
        } else {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                String username = args[0];

                Player target = MultiChat.getInstance().getServer().getPlayer(username).orElse(null);

                if (target != null) {

                    if (target.getUsername().equals(player.getUsername())) {
                        MessageManager.sendMessage(player, "ignore_cannot_ignore_yourself");
                        return;
                    }

                    if (target.hasPermission("multichat.ignore.bypass")) {
                        MessageManager.sendMessage(player, "ignore_bypass");
                        return;
                    }

                    if (!ChatControl.ignoresAnywhere(target.getUniqueId(), (player).getUniqueId())) {
                        ChatControl.ignore(player.getUniqueId(), target.getUniqueId());
                        MessageManager.sendSpecialMessage(player, "ignore_ignored", target.getUsername());
                    } else {
                        ChatControl.unignore(player.getUniqueId(), target.getUniqueId());
                        MessageManager.sendSpecialMessage(player, "ignore_unignored", target.getUsername());
                    }

                    BungeeComm.sendIgnoreMap(player.getCurrentServer().get().getServerInfo());

                } else {

                    MessageManager.sendMessage(sender, "ignore_player_not_found");

                }

            } else {

                MessageManager.sendMessage(sender, "ignore_only_players");

            }

        }

    }

}
