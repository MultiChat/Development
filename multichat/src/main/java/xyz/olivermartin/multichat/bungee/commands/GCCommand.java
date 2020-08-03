package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ChatManipulation;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Group Chat Messaging Command
 * <p>Allows players to send a message direct to a group chat or toggle group chats</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GCCommand extends Command {

	public GCCommand() {
		super("mcgc", "multichat.group", (String[]) ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("gc").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		if (args.length < 1) {

			if ((sender instanceof ProxiedPlayer)) {

				ProxiedPlayer player = (ProxiedPlayer)sender;
				boolean toggleresult = Events.toggleGC(player.getUniqueId());

				if (toggleresult == true) {
					MessageManager.sendMessage(sender, "command_gc_toggle_on");
				} else {
					MessageManager.sendMessage(sender, "command_gc_toggle_off");
				}

			} else {

				MessageManager.sendMessage(sender, "command_gc_only_players_toggle");
			}

		} else if ((sender instanceof ProxiedPlayer)) {

			ProxiedPlayer player = (ProxiedPlayer)sender;

			if (ds.getViewedChats().get(player.getUniqueId()) != null) {

				String groupName = (String)ds.getViewedChats().get(player.getUniqueId());

				if (ds.getGroupChats().containsKey(groupName)) {

					TGroupChatInfo groupInfo = (TGroupChatInfo) ds.getGroupChats().get(groupName);

					String message = MultiChatUtil.getMessageFromArgs(args);

					String playerName = sender.getName();

					if ((groupInfo.getFormal() == true)
							&& (groupInfo.getAdmins().contains(player.getUniqueId()))) {
						playerName = "&o" + playerName;
					}

					sendMessage(message, playerName, groupInfo);

				} else {

					MessageManager.sendMessage(sender, "command_gc_no_longer_exists");
				}

			} else {
				MessageManager.sendMessage(sender, "command_gc_no_chat_selected");
			}

		} else {
			MessageManager.sendMessage(sender, "command_gc_only_players_speak");
		}
	}

	public static void sendMessage(String originalMessage, String playerName, TGroupChatInfo groupInfo) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ChatManipulation chatfix = new ChatManipulation();

		ProxiedPlayer potentialPlayer = ProxyServer.getInstance().getPlayer(playerName);
		if (potentialPlayer != null) {
			if (ChatControl.isMuted(potentialPlayer.getUniqueId(), "group_chats")) {
				MessageManager.sendMessage(potentialPlayer, "mute_cannot_send_message");
				return;
			}

			if (ChatControl.handleSpam(potentialPlayer, originalMessage, "group_chats")) {
				return;
			}
		}

		Optional<String> crm;

		crm = ChatControl.applyChatRules(originalMessage, "group_chats", playerName);

		if (crm.isPresent()) {
			originalMessage = crm.get();
		} else {
			return;
		}

		String messageFormat = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString(ConfigValues.Config.GroupChat.FORMAT);
		String message = chatfix.replaceGroupChatVars(messageFormat, playerName, originalMessage, groupInfo.getName());

		message = MultiChatUtil.translateColourCodes(message);
		String originalTranslated = MultiChatUtil.translateColourCodes(originalMessage);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (((groupInfo.existsViewer(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.group"))) || ((ds.getAllSpy().contains(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.staff.spy")))) {

				if (potentialPlayer != null) {
					if (!ChatControl.ignores(potentialPlayer.getUniqueId(), onlineplayer.getUniqueId(), "group_chats")) {
						if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
							onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColourCodes(message), "%MESSAGE%", MultiChatUtil.approximateRGBColourCodes(originalTranslated)));
						} else {
							onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message, "%MESSAGE%", originalTranslated));
						}
					} else {
						ChatControl.sendIgnoreNotifications(onlineplayer, potentialPlayer, "group_chats");
					}
				} else {
					if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
						onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColourCodes(message), "%MESSAGE%", MultiChatUtil.approximateRGBColourCodes(originalTranslated)));
					} else {
						onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message, "%MESSAGE%", originalTranslated));
					}
				}

			}

		}

		BaseComponent[] finalMessage = ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColourCodes(message), "%MESSAGE%", MultiChatUtil.approximateRGBColourCodes(originalTranslated));
		String consoleMessage = "";
		for (BaseComponent bc : finalMessage) consoleMessage += bc.toLegacyText();
		ConsoleManager.logGroupChat(consoleMessage);

	}
}
