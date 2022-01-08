package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.*;

import java.util.Optional;

/**
 * Global Command
 * <p>Causes players to see messages sent from all servers in the global chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GlobalCommand extends Command {

    public GlobalCommand() {
        super("global", ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("globalcommand").getList(String::valueOf).toArray(new String[0]));
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.chat.mode");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if ((sender instanceof Player)) {

            if (args.length < 1) {

                ChatModeManager.getInstance().setGlobal(((Player) sender).getUniqueId());

                MessageManager.sendMessage(sender, "command_global_enabled_1");
                MessageManager.sendMessage(sender, "command_global_enabled_2");

            } else {

                Player player = (Player) sender;
                String message = MultiChatUtil.getMessageFromArgs(args);

                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("global").getBoolean()) {

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_global").getList(String::valueOf).contains(player.getCurrentServer().get().getServerInfo().getName())) {

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
                            Channel global = Channel.getGlobalChannel();
                            if (!global.isMember(player.getUniqueId())) {
                                global.removeMember(player.getUniqueId());
                                MessageManager.sendSpecialMessage(player, "command_channel_show", "GLOBAL");
                            }

                            // Let server know players channel preference
                            BungeeComm.sendPlayerChannelMessage(player.getUsername(),
                                    Channel.getChannel(player.getUniqueId()).getName(),
                                    Channel.getChannel(player.getUniqueId()),
                                    player.getCurrentServer().get().getServerInfo(),
                                    (player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.color.simple")),
                                    (player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.color.rgb")));

                            // Message passes through to spigot here

                            // Send message directly to global chat...

                            BungeeComm.sendPlayerCommandMessage("!SINGLE G MESSAGE!" + message, ((Player) sender).getUsername(), ((Player) sender).getCurrentServer().get().getServerInfo());

                            Events.hiddenStaff.remove(player.getUniqueId());

                        } else {
                            MessageManager.sendMessage(player, "freezechat_frozen");
                        }

                    }
                }

            }

        } else {
            MessageManager.sendMessage(sender, "command_global_only_players");
        }
    }
}
