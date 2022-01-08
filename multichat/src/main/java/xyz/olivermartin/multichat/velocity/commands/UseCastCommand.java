package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.olivermartin.multichat.velocity.CastControl;
import xyz.olivermartin.multichat.velocity.Channel;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;

/**
 * Use Cast Command
 * <p>A command designed to allow you to use a cast from the console</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class UseCastCommand extends Command {

    private static final String[] aliases = new String[]{};

    public UseCastCommand() {
        super("usecast", aliases);
    }

    public void displayUsage(CommandSource sender) {
        MessageManager.sendMessage(sender, "command_usecast_usage");
        sender.sendMessage(Component.text("/usecast <name> <message>").color(NamedTextColor.AQUA));
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.cast.admin");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 2) {
            displayUsage(sender);
            return;
        }

        if (CastControl.existsCast(args[0])) {
            String message = MultiChatUtil.getMessageFromArgs(args, 1);
            CastControl.sendCast(args[0], message, Channel.getGlobalChannel(), sender);
        } else {
            MessageManager.sendSpecialMessage(sender, "command_usecast_does_not_exist", args[0].toUpperCase());
        }
    }
}
