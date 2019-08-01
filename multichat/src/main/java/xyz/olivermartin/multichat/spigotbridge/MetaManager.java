package xyz.olivermartin.multichat.spigotbridge;

import org.bukkit.Bukkit;

import net.milkbowl.vault.chat.Chat;

public class MetaManager {

	private static MetaManager instance;

	public static MetaManager getInstance() {
		return instance;
	}

	static {
		instance = new MetaManager();
	}

	/* --- END STATIC --- */

	private MetaManager() {
		/* Empty */
	}

	public void updatePlayerMeta(String playername, boolean setDisplayName, String displayNameFormat) {

		String nickname;

		nickname = NameManager.getInstance().getCurrentName(Bukkit.getPlayer(playername).getUniqueId());

		SpigotCommunicationManager comm = SpigotCommunicationManager.getInstance();

		comm.sendPluginChannelMessage("multichat:nick", Bukkit.getPlayer(playername).getUniqueId(), nickname);
		comm.sendPluginChannelMessage("multichat:world", Bukkit.getPlayer(playername).getUniqueId(), Bukkit.getPlayer(playername).getWorld().getName());

		if (MultiChatSpigot.hookedVault()) {

			Chat chat = MultiChatSpigot.getVaultChat().get();

			comm.sendPluginChannelMessage("multichat:prefix", Bukkit.getPlayer(playername).getUniqueId(), chat.getPlayerPrefix(Bukkit.getPlayer(playername)));
			comm.sendPluginChannelMessage("multichat:suffix", Bukkit.getPlayer(playername).getUniqueId(), chat.getPlayerSuffix(Bukkit.getPlayer(playername)));

			if (setDisplayName) {

				displayNameFormat = displayNameFormat.replaceAll("%NICK%", nickname);
				displayNameFormat = displayNameFormat.replaceAll("%NAME%", playername);
				displayNameFormat = displayNameFormat.replaceAll("%PREFIX%", chat.getPlayerPrefix(Bukkit.getPlayer(playername)));
				displayNameFormat = displayNameFormat.replaceAll("%SUFFIX%", chat.getPlayerSuffix(Bukkit.getPlayer(playername)));
				displayNameFormat = displayNameFormat.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");

				Bukkit.getPlayer(playername).setDisplayName(displayNameFormat);
				Bukkit.getPlayer(playername).setPlayerListName(displayNameFormat);
			}
			
			comm.sendPluginChannelMessage("multichat:dn", Bukkit.getPlayer(playername).getUniqueId(), Bukkit.getPlayer(playername).getDisplayName());
			
		} else {

			if (setDisplayName) {

				displayNameFormat = displayNameFormat.replaceAll("%NICK%", nickname);
				displayNameFormat = displayNameFormat.replaceAll("%NAME%", playername);
				displayNameFormat = displayNameFormat.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");

				Bukkit.getPlayer(playername).setDisplayName(displayNameFormat);
				Bukkit.getPlayer(playername).setPlayerListName(displayNameFormat);

			}
			
			comm.sendPluginChannelMessage("multichat:dn", Bukkit.getPlayer(playername).getUniqueId(), Bukkit.getPlayer(playername).getDisplayName());

		}

	}

}
