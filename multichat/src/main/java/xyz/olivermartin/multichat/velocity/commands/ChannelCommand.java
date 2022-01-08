package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.*;

/**
 * Chat Channel Command
 * <p>Players can use this command to switch channels, as well as show and hide specific channels</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ChannelCommand extends Command {

    public ChannelCommand() {
        super("channel", ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("channelcommand").getList(String::valueOf).toArray(new String[0]));
    }

    private void showHelp(CommandSource sender) {
        MessageManager.sendMessage(sender, "command_channel_help");
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.chat.channel");
    }

    public void execute(Invocation invocation) {

        var args = invocation.arguments();
        var sender = invocation.source();

        if ((sender instanceof Player)) {
            if ((args.length < 1) || ((args.length == 1) && (args[0].toLowerCase().equals("help")))) {
                showHelp(sender);
            } else if (args.length == 1) {
                showHelp(sender);
            } else if (args.length == 2) {
                String subCommand = args[0].toLowerCase();
                String operand = args[1].toLowerCase();

                switch (subCommand) {
                    case "switch":
                        if (!sender.hasPermission("multichat.chat.channel.switch")) {
                            MessageManager.sendMessage(sender, "command_channel_switch_no_permission");
                            return;
                        }
                        if (operand.equals("local")) {
                            ChatModeManager.getInstance().setLocal(((Player) sender).getUniqueId());
                            MessageManager.sendSpecialMessage(sender, "command_channel_switch", operand.toUpperCase());
                        } else if (operand.equals("global")) {
                            ChatModeManager.getInstance().setGlobal(((Player) sender).getUniqueId());
                            MessageManager.sendSpecialMessage(sender, "command_channel_switch", operand.toUpperCase());
                        } else {
                            MessageManager.sendMessage(sender, "command_channel_does_not_exist");
                        }

                    case "hide":
                        if (!sender.hasPermission("multichat.chat.channel.hide")) {
                            MessageManager.sendMessage(sender, "command_channel_hide_no_permission");
                            return;
                        }
                        if (operand.equals("local")) {

                            if (!ChatModeManager.getInstance().isGlobal(((Player) sender).getUniqueId())) {
                                MessageManager.sendMessage(sender, "command_channel_cannot_hide");
                                return;
                            }

                            Channel local = Channel.getLocalChannel();
                            if (local.isMember(((Player) sender).getUniqueId())) {
                                local.addMember(((Player) sender).getUniqueId());
                                MessageManager.sendSpecialMessage(sender, "command_channel_hide", operand.toUpperCase());
                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_channel_already_hide", operand.toUpperCase());
                            }

                        } else if (operand.equals("global")) {

                            if (ChatModeManager.getInstance().isGlobal(((Player) sender).getUniqueId())) {
                                MessageManager.sendMessage(sender, "command_channel_cannot_hide");
                                return;
                            }

                            Channel global = Channel.getGlobalChannel();
                            if (global.isMember(((Player) sender).getUniqueId())) {
                                global.addMember(((Player) sender).getUniqueId());
                                MessageManager.sendSpecialMessage(sender, "command_channel_hide", operand.toUpperCase());
                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_channel_already_hide", operand.toUpperCase());
                            }

                        } else {
                            MessageManager.sendMessage(sender, "command_channel_does_not_exist");
                        }

                    case "show":
                        if (!sender.hasPermission("multichat.chat.channel.show")) {
                            MessageManager.sendMessage(sender, "command_channel_show_no_permission");
                            return;
                        }
                        if (operand.equals("local")) {

                            Channel local = Channel.getLocalChannel();
                            if (!local.isMember(((Player) sender).getUniqueId())) {
                                local.removeMember(((Player) sender).getUniqueId());
                                MessageManager.sendSpecialMessage(sender, "command_channel_show", operand.toUpperCase());
                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_channel_already_show", operand.toUpperCase());
                            }

                        } else if (operand.equals("global")) {

                            Channel global = Channel.getGlobalChannel();
                            if (!global.isMember(((Player) sender).getUniqueId())) {
                                global.removeMember(((Player) sender).getUniqueId());
                                MessageManager.sendSpecialMessage(sender, "command_channel_show", operand.toUpperCase());
                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_channel_already_show", operand.toUpperCase());
                            }

                        } else {
                            MessageManager.sendMessage(sender, "command_channel_does_not_exist");
                        }

                    default:
                        showHelp(sender);
                        break;
                }

                // Update local channel info
                for (Player p : MultiChat.getInstance().getServer().getAllPlayers()) {
                    BungeeComm.sendPlayerChannelMessage(
                            p.getUsername(),
                            Channel.getChannel(p.getUniqueId()).getName(),
                            Channel.getChannel(p.getUniqueId()),
                            p.getCurrentServer().get().getServerInfo(),
                            (p.hasPermission("multichat.chat.color") || p.hasPermission("multichat.chat.color.simple")),
                            (p.hasPermission("multichat.chat.color") || p.hasPermission("multichat.chat.color.rgb")));
                }

            }

        } else {
            MessageManager.sendMessage(sender, "command_channel_only_players");
        }

    }

}
