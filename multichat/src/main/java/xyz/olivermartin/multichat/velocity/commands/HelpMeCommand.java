package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.*;

import java.util.Optional;

/**
 * 'Help Me' Command
 * <p>Allows players to request help from all online staff members</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class HelpMeCommand extends Command {

    private static final String[] aliases = new String[]{};

    public HelpMeCommand() {
        super("helpme", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.chat.helpme");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {

                MessageManager.sendMessage(sender, "command_helpme_desc");
                MessageManager.sendMessage(sender, "command_helpme_usage");

            } else {

                String message = MultiChatUtil.getMessageFromArgs(args);

                if (sendMessage(player.getUsername() + ": " + message, player.getUsername())) {
                    MessageManager.sendMessage(sender, "command_helpme_sent");
                }

            }

        } else {
            MessageManager.sendMessage(sender, "command_helpme_only_players");
        }
    }

    public static boolean sendMessage(String message, String username) {

        Optional<String> crm;

        Player potentialPlayer = MultiChat.getInstance().getServer().getPlayer(username).orElse(null);
        if (potentialPlayer != null) {
            if (ChatControl.isMuted(potentialPlayer.getUniqueId(), "helpme")) {
                MessageManager.sendMessage(potentialPlayer, "mute_cannot_send_message");
                return false;
            }

            if (ChatControl.handleSpam(potentialPlayer, message, "helpme")) {
                return false;
            }
        }

        crm = ChatControl.applyChatRules(message, "helpme", username);

        if (crm.isPresent()) {
            message = crm.get();
        } else {
            return false;
        }

        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
            if (onlineplayer.hasPermission("multichat.staff")) {
                MessageManager.sendSpecialMessage(onlineplayer, "command_helpme_format", message);
            }
        }

        ConsoleManager.logHelpMe(message);

        return true;

    }
}
