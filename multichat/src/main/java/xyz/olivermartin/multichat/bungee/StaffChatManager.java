package xyz.olivermartin.multichat.bungee;

import java.util.Optional;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.events.MultiChatStaffIRCEvent;

/**
 * Staff Chat Manager
 * <p>Manages chat input to the staff chats, both mod and admin</p>
 * 
 * @author Oliver Martin (Revilo410)
 */
public class StaffChatManager {

	public void sendModMessage(String username, String displayname, String server, String message) {

		ChatManipulation chatfix = new ChatManipulation();
		String messageFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.format");
		String original = message;

		Optional<String> crm;

		crm = ChatControl.applyChatRules(original, "staff_chats", username);

		if (crm.isPresent()) {
			original = crm.get();
		} else {
			return;
		}

		// Alert IRC plugins
		if (username.equalsIgnoreCase("console")) {
			ProxyServer.getInstance().getPluginManager().callEvent(new MultiChatStaffIRCEvent("mod", ProxyServer.getInstance().getConsole() , original));
		} else {
			if (ProxyServer.getInstance().getPlayer(username) != null) {
				ProxyServer.getInstance().getPluginManager().callEvent(new MultiChatStaffIRCEvent("mod", ProxyServer.getInstance().getPlayer(username) , original));
			}
		}

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (onlineplayer.hasPermission("multichat.staff.mod")) {

				if (!MultiChat.modchatpreferences.containsKey(onlineplayer.getUniqueId())) {

					TChatInfo chatinfo = new TChatInfo();
					chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.ccdefault").toCharArray()[0]);
					chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.ncdefault").toCharArray()[0]);

					MultiChat.modchatpreferences.put(onlineplayer.getUniqueId(), chatinfo);

				}

				message = chatfix.replaceModChatVars(messageFormat, username, displayname, server, original, onlineplayer);
				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));

			}
		}

		System.out.println("\033[36m[StaffChat] /mc {" + username + "}  " + original);

	}

	public void sendAdminMessage(String username, String displayname, String server, String message) {

		String original = message;
		ChatManipulation chatfix = new ChatManipulation();
		String messageFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.format");

		Optional<String> crm;

		crm = ChatControl.applyChatRules(original, "staff_chats", username);

		if (crm.isPresent()) {
			original = crm.get();
		} else {
			return;
		}

		// Alert IRC plugins
		if (username.equalsIgnoreCase("console")) {
			ProxyServer.getInstance().getPluginManager().callEvent(new MultiChatStaffIRCEvent("admin", ProxyServer.getInstance().getConsole() , original));
		} else {
			if (ProxyServer.getInstance().getPlayer(username) != null) {
				ProxyServer.getInstance().getPluginManager().callEvent(new MultiChatStaffIRCEvent("admin", ProxyServer.getInstance().getPlayer(username) , original));
			}
		}

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (onlineplayer.hasPermission("multichat.staff.admin")) {

				if (!MultiChat.adminchatpreferences.containsKey(onlineplayer.getUniqueId())) {

					TChatInfo chatinfo = new TChatInfo();
					chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.ccdefault").toCharArray()[0]);
					chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.ncdefault").toCharArray()[0]);

					MultiChat.adminchatpreferences.put(onlineplayer.getUniqueId(), chatinfo);

				}

				message = chatfix.replaceAdminChatVars(messageFormat, username, displayname, server, original, onlineplayer);
				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));

			}
		}

		System.out.println("\033[35m[StaffChat] /ac {" + username + "}  " + original);

	}
}
