package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.*;

import java.util.Optional;

/**
 * Local Chat Command
 * <p>Players can use this command to only see the chat sent from players on their current server</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class LocalCommand extends Command {

    public LocalCommand() {
        super("local", ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("localcommand").getList(String::valueOf).toArray(new String[0]));
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.chat.mode");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 1) {

                ChatModeManager.getInstance().setLocal(player.getUniqueId());

                MessageManager.sendMessage(sender, "command_local_enabled_1");
                MessageManager.sendMessage(sender, "command_local_enabled_2");

            } else {

                String message = MultiChatUtil.getMessageFromArgs(args);

                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                    BungeeComm.sendMessage(player.getUsername(), player.getCurrentServer().get().getServerInfo());
                }

                if ((!MultiChat.frozen) || (player.hasPermission("multichat.chat.always"))) {

                    if (ChatControl.isMuted(player.getUniqueId(), "global_chat")) {
                        MessageManager.sendMessage(player, "mute_cannot_send_message");
                        return;
                    }

                    DebugManager.log(player.getUsername() + "- about to check for spam");

                    if (ChatControl.handleSpam(player, message, "global_chat")) {
                        DebugManager.log(player.getUsername() + " - chat message being cancelled due to spam");
                        return;
                    }

                    Optional<String> crm;

                    crm = ChatControl.applyChatRules(message, "global_chat", player.getUsername());

                    if (crm.isPresent()) {
                        message = crm.get();
                    } else {
                        return;
                    }

                    if (!player.hasPermission("multichat.chat.link")) {
                        message = ChatControl.replaceLinks(message);
                    }

                    // If they had this channel hidden, then unhide it...
                    Channel local = Channel.getLocalChannel();
                    if (!local.isMember(player.getUniqueId())) {
                        local.removeMember(player.getUniqueId());
                        MessageManager.sendSpecialMessage(player, "command_channel_show", "LOCAL");
                    }

                    // Let server know players channel preference
                    BungeeComm.sendPlayerChannelMessage(player.getUsername(),
                            Channel.getChannel(player.getUniqueId()).getName(),
                            Channel.getChannel(player.getUniqueId()),
                            player.getCurrentServer().get().getServerInfo(),
                            (player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.color.simple")),
                            (player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.color.rgb")));

                    // Message passes through to spigot here
                    // Send message directly to local chat...

                    BungeeComm.sendPlayerCommandMessage("!SINGLE L MESSAGE!" + message, player.getUsername(), player.getCurrentServer().get().getServerInfo());

                    Events.hiddenStaff.remove(player.getUniqueId());

                } else {
                    MessageManager.sendMessage(player, "freezechat_frozen");
                }

            }

        } else {
            MessageManager.sendMessage(sender, "command_local_only_players");
        }
    }
}
