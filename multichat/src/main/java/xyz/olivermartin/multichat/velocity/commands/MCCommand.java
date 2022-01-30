package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.Events;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.StaffChatManager;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;

/**
 * Mod-Chat Commands
 * <p>Allows staff members to send mod-chat messages or toggle the chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MCCommand extends Command {

    private static final String[] aliases = new String[]{};

    public MCCommand() {
        super("mc", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.staff.mod");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        boolean toggleresult;

        if (args.length < 1) {

            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                toggleresult = Events.toggleMC(player.getUniqueId());

                if (toggleresult) {
                    MessageManager.sendMessage(sender, "command_mc_toggle_on");
                } else {
                    MessageManager.sendMessage(sender, "command_mc_toggle_off");
                }

            } else {
                MessageManager.sendMessage(sender, "command_mc_only_players");
            }

        } else if ((sender instanceof Player)) {
            Player player = (Player) sender;

            String message = MultiChatUtil.getMessageFromArgs(args);

            StaffChatManager chatman = new StaffChatManager();
            chatman.sendModMessage(player.getUsername(), player.getUsername(), player.getCurrentServer().get().getServerInfo().getName(), message);

        } else {

            String message = MultiChatUtil.getMessageFromArgs(args);

            StaffChatManager chatman = new StaffChatManager();
            chatman.sendModMessage("CONSOLE", "CONSOLE", "#", message);
        }
    }
}
