package xyz.olivermartin.multichat.velocity.commands;

import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;

/**
 * Group List Command
 * <p>Displays a list of all current group chats on the server</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GroupListCommand extends Command {

    private static final String[] aliases = new String[]{};

    public GroupListCommand() {
        super("groups", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.staff.listgroups");
    }

    public void execute(Invocation invocation) {
        var sender = invocation.source();

        MessageManager.sendMessage(sender, "command_grouplist_list");

        for (String groupname : MultiChat.groupchats.keySet()) {
            MessageManager.sendSpecialMessage(sender, "command_grouplist_list_item", groupname);
        }

    }
}
