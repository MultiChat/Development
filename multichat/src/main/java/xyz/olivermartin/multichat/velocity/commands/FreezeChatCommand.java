package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;

/**
 * Freeze Chat Command
 * <p>Allows staff members to block all chat messages being sent</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class FreezeChatCommand extends Command {

    private static final String[] aliases = new String[]{};

    public FreezeChatCommand() {
        super("freezechat", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.chat.freeze");
    }

    public void execute(Invocation invocation) {
        var sender = invocation.source();
        String senderName = "";

        if (sender instanceof Player) {
            senderName = ((Player) sender).getUsername();
        } else {
            senderName = "CONSOLE";
        }

        if (MultiChat.frozen) {

            for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
                MessageManager.sendSpecialMessage(onlineplayer, "command_freezechat_thawed", senderName);
            }

            MultiChat.frozen = false;

        } else {

            for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
                MessageManager.sendSpecialMessage(onlineplayer, "command_freezechat_frozen", senderName);
            }

            MultiChat.frozen = true;
        }
    }
}
