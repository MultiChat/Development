package xyz.olivermartin.multichat.local.spigot;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.milkbowl.vault.chat.Chat;
import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.LocalMetaManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotVaultHook;

public class LocalSpigotMetaManager extends LocalMetaManager {

	@Override
	public String getPrefix(UUID uuid) {

		Optional<Chat> opVault = LocalSpigotVaultHook.getInstance().getHook();

		if (opVault.isPresent()) {

			Chat vaultChat = opVault.get();

			// Get prefix
			String prefix = vaultChat.getPlayerPrefix(Bukkit.getServer().getPlayer(uuid));

			// Translate prefix
			prefix = MultiChatLocal.getInstance().getChatManager().translateColorCodes(prefix, true); 

			return prefix;

		}

		return "";

	}

	@Override
	public String getSuffix(UUID uuid) {

		Optional<Chat> opVault = LocalSpigotVaultHook.getInstance().getHook();

		if (opVault.isPresent()) {

			Chat vaultChat = opVault.get();

			// Get suffix
			String suffix = vaultChat.getPlayerSuffix(Bukkit.getServer().getPlayer(uuid));

			// Translate suffix
			suffix = MultiChatLocal.getInstance().getChatManager().translateColorCodes(suffix, true); 

			return suffix;

		}

		return "";

	}

	@Override
	public String getWorld(UUID uuid) {

		Player player = Bukkit.getPlayer(uuid);

		if (player == null) return "";

		return player.getWorld().getName();

	}

	@Override
	public String getDisplayName(UUID uuid) {

		LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		Player player = Bukkit.getPlayer(uuid);

		if (player == null) return "";

		// If MultiChat is setting the display name...
		if (MultiChatLocal.getInstance().getDataStore().isSetDisplayName()) {

			logger.debug("[LocalSpigotMetaManager] We are setting the display name!");

			String displayNameFormat = MultiChatLocal.getInstance().getDataStore().getDisplayNameFormatLastVal();

			// TODO This stuff could be refactored as it is duplicated between Spigot and Sponge
			displayNameFormat = displayNameFormat.replaceAll("%NICK%", getNick(uuid));
			displayNameFormat = displayNameFormat.replaceAll("%NAME%", player.getName());
			displayNameFormat = displayNameFormat.replaceAll("%PREFIX%", getPrefix(uuid));
			displayNameFormat = displayNameFormat.replaceAll("%SUFFIX%", getSuffix(uuid));

			// Translate displayname
			displayNameFormat = MultiChatLocal.getInstance().getChatManager().translateColorCodes(displayNameFormat, true); 

			player.setDisplayName(displayNameFormat);
			player.setPlayerListName(displayNameFormat);

		}

		return player.getDisplayName();

	}

}
