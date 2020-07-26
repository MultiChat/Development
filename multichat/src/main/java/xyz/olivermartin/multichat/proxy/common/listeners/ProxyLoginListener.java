package xyz.olivermartin.multichat.proxy.common.listeners;

import java.util.UUID;

import com.olivermartin410.plugins.TChatInfo;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.bungee.ChatManipulation;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.bungee.UUIDNameManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

public class ProxyLoginListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PostLoginEvent event) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

		ProxiedPlayer player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		// Set up modchat info
		if (player.hasPermission("multichat.staff.mod")) {

			if (!ds.getModChatPreferences().containsKey(uuid)) {

				TChatInfo chatinfo = new TChatInfo();
				chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.ccdefault").toCharArray()[0]);
				chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.ncdefault").toCharArray()[0]);
				ds.getModChatPreferences().put(uuid, chatinfo);

			}
		}

		// Set up adminchat info
		if (player.hasPermission("multichat.staff.admin")) {

			if (!ds.getAdminChatPreferences().containsKey(uuid)) {

				TChatInfo chatinfo = new TChatInfo();
				chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.ccdefault").toCharArray()[0]);
				chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.ncdefault").toCharArray()[0]);
				ds.getAdminChatPreferences().put(uuid, chatinfo);

			}
		}

		// Register player in volatile meta manager
		PlayerMetaManager.getInstance().registerPlayer(uuid, event.getPlayer().getName());

		// Set up groupchat info
		if (!ds.getViewedChats().containsKey(uuid)) {

			ds.getViewedChats().put(uuid, null);
			ConsoleManager.log("Registered player " + player.getName());

		}

		String defaultChannel = MultiChatProxy.getInstance().getContextManager().getGlobalContext().getDefaultChannel();
		boolean forceChannel = MultiChatProxy.getInstance().getContextManager().getGlobalContext().isForceChannel();
		boolean firstJoin = false;

		// Set up chat info
		if (!ChatModeManager.getInstance().existsPlayer(uuid)) {

			boolean globalMode;

			if (!defaultChannel.equalsIgnoreCase("local")) {
				globalMode = true;
			} else {
				globalMode = false;
			}
			ChatModeManager.getInstance().registerPlayer(uuid, globalMode);
			firstJoin = true;

		}

		// If we are forcing the channels, then force it
		if (forceChannel) {

			boolean globalMode;
			if (!defaultChannel.equalsIgnoreCase("local")) {
				globalMode = true;
			} else {
				globalMode = false;
			}
			ChatModeManager.getInstance().registerPlayer(uuid, globalMode);

		}

		// Set player to appropriate channels in NEW CHANNELS system
		if (ChatModeManager.getInstance().isGlobal(uuid)) {
			channelManager.select(uuid, "global");
		} else {
			channelManager.select(uuid, "local");
		}

		// Remove any old UUID - Name pairings
		if (UUIDNameManager.existsUUID(uuid)) {
			UUIDNameManager.removeUUID(uuid);
		}

		// Register updated entry in UUID - Name map
		UUIDNameManager.addNew(uuid, player.getName());
		ConsoleManager.log("Refreshed UUID-Name lookup: " + uuid.toString());

		// If MultiChat is handling join messages...
		if ( ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getBoolean("showjoin") == true ) {

			// PremiumVanish support, return as early as possible to avoid loading unnecessary resources
			if (MultiChat.premiumVanish && MultiChat.hideVanishedStaffInJoin && BungeeVanishAPI.isInvisible(player)) {
				return;
			}

			// Load join message formats from config
			String joinformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("serverjoin");
			String silentformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("silentjoin");
			String welcomeMessage = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("welcome_message");
			String privateWelcomeMessage = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("private_welcome_message");

			// Replace the placeholders
			ChatManipulation chatman = new ChatManipulation(); // TODO Legacy
			joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName());
			silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName());
			welcomeMessage = chatman.replaceJoinMsgVars(welcomeMessage, player.getName());
			privateWelcomeMessage = chatman.replaceJoinMsgVars(privateWelcomeMessage, player.getName());

			// Check which messages should be broadcast
			boolean broadcastWelcome = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getBoolean("welcome", true);
			boolean privateWelcome = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getBoolean("private_welcome", false);
			boolean broadcastJoin = !player.hasPermission("multichat.staff.silentjoin");

			// Broadcast
			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

				if (broadcastJoin) {

					if (firstJoin && broadcastWelcome) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', welcomeMessage)));
					}

					if (firstJoin && privateWelcome && onlineplayer.getName().equals(player.getName())) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', privateWelcomeMessage)));
					}

					onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', joinformat)));

				} else {

					ds.getHiddenStaff().add(player.getUniqueId());

					if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', silentformat)));
					}

				}
			}
		}
	}

}
