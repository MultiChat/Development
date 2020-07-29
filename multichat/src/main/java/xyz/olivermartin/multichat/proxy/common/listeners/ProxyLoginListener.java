package xyz.olivermartin.multichat.proxy.common.listeners;

import java.util.UUID;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

public class ProxyLoginListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PostLoginEvent event) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		ProxiedPlayer player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		// Set up modchat info
		if (player.hasPermission("multichat.staff.mod")) {

			if (!ds.getModChatPreferences().containsKey(uuid)) {

				TChatInfo chatinfo = new TChatInfo();
				chatinfo.setChatColor(ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString("modchat.ccdefault").toCharArray()[0]);
				chatinfo.setNameColor(ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString("modchat.ncdefault").toCharArray()[0]);
				ds.getModChatPreferences().put(uuid, chatinfo);

			}
		}

		// Set up adminchat info
		if (player.hasPermission("multichat.staff.admin")) {

			if (!ds.getAdminChatPreferences().containsKey(uuid)) {

				TChatInfo chatinfo = new TChatInfo();
				chatinfo.setChatColor(ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString("adminchat.ccdefault").toCharArray()[0]);
				chatinfo.setNameColor(ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString("adminchat.ncdefault").toCharArray()[0]);
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

	}

}
