package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.ConfigManager;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;

/**
 * SocialSpy Command
 * <p>Allows staff members to view private messages sent by players</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class SocialSpyCommand extends Command {

    public SocialSpyCommand() {
        super("socialspy", ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("socialspycommand").getList(String::valueOf).toArray(new String[0]));
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.staff.spy");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if ((sender instanceof Player)) {

            if (args.length < 1) {

                if (MultiChat.socialspy.contains(((Player) sender).getUniqueId())) {
                    MultiChat.socialspy.remove(((Player) sender).getUniqueId());
                    MessageManager.sendMessage(sender, "command_socialspy_disabled");
                } else {
                    MultiChat.socialspy.add(((Player) sender).getUniqueId());
                    MessageManager.sendMessage(sender, "command_socialspy_enabled");
                }

            } else {
                MessageManager.sendMessage(sender, "command_socialspy_usage");
                MessageManager.sendMessage(sender, "command_socialspy_desc");
            }

        } else {
            MessageManager.sendMessage(sender, "command_socialspy_only_players");
        }
    }
}
