package xyz.olivermartin.multichat.bungee;

import java.util.Optional;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.events.PostStaffChatEvent;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Staff Chat Manager
 * <p>Manages chat input to the staff chats, both mod and admin</p>
 * 
 * @author Oliver Martin (Revilo410)
 */
public class StaffChatManager {

	public void sendModMessage(String username, String displayname, String server, String message) {
		sendStaffChatMessage("mod", username, displayname, server, message);
	}

	public void sendAdminMessage(String username, String displayname, String server, String message) {
		sendStaffChatMessage("admin", username, displayname, server, message);
	}

	private void sendStaffChatMessage(String id, String username, String displayname, String server, String message) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		ChatManipulation chatfix = new ChatManipulation();
		String messageFormat = id.equals("mod")
				? ProxyConfigs.CONFIG.getModChatFormat()
				: ProxyConfigs.CONFIG.getAdminChatFormat();
		String original = message;

		Optional<String> crm;

		ProxiedPlayer proxiedPlayer = username.equals("console") ? null : ProxyServer.getInstance().getPlayer(username);
		crm = ChatControl.applyChatRules(proxiedPlayer, original, MessageType.STAFF_CHATS);

		if (crm.isPresent()) {
			original = crm.get();
		} else {
			return;
		}

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (onlineplayer.hasPermission("multichat.staff." + id)) {

				if (id.equals("mod") && !ds.getModChatPreferences().containsKey(onlineplayer.getUniqueId())) {

					TChatInfo chatinfo = new TChatInfo();
					chatinfo.setChatColor(ProxyConfigs.CONFIG.getModChatColor());
					chatinfo.setNameColor(ProxyConfigs.CONFIG.getModNameColor());

					ds.getModChatPreferences().put(onlineplayer.getUniqueId(), chatinfo);

				} else if (id.equals("admin") && !ds.getAdminChatPreferences().containsKey(onlineplayer.getUniqueId())) {

					TChatInfo chatinfo = new TChatInfo();
					chatinfo.setChatColor(ProxyConfigs.CONFIG.getAdminChatColor());
					chatinfo.setNameColor(ProxyConfigs.CONFIG.getAdminNameColor());

					ds.getAdminChatPreferences().put(onlineplayer.getUniqueId(), chatinfo);

				}

				if (id.equals("mod")) {
					message = chatfix.replaceModChatVars(messageFormat, username, displayname, server, original, onlineplayer);
				} else {
					message = chatfix.replaceAdminChatVars(messageFormat, username, displayname, server, original, onlineplayer);
				}

				message = MultiChatUtil.translateColorCodes(message);
				String originalTranslated = MultiChatUtil.translateColorCodes(original);

				if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
					message = MultiChatUtil.approximateRGBColorCodes(message);
					originalTranslated = MultiChatUtil.approximateRGBColorCodes(originalTranslated);
				}

				onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message, "%MESSAGE%", originalTranslated));

			}
		}

		// Trigger PostStaffChatEvent
		if (username.equalsIgnoreCase("console")) {
			ProxyServer.getInstance().getPluginManager().callEvent(new PostStaffChatEvent(id, ProxyServer.getInstance().getConsole() , original));
		} else {
			if (ProxyServer.getInstance().getPlayer(username) != null) {
				ProxyServer.getInstance().getPluginManager().callEvent(new PostStaffChatEvent(id, ProxyServer.getInstance().getPlayer(username) , original));
			}
		}

		if (id.equals("mod")) {
			ConsoleManager.logModChat("(" + username + ") " + original);
		} else {
			ConsoleManager.logAdminChat("(" + username + ") " + original);
		}

	}

}
