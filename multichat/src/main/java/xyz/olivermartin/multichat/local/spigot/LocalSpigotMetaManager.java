package xyz.olivermartin.multichat.local.spigot;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.milkbowl.vault.chat.Chat;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.LocalMetaManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotVaultHook;

public class LocalSpigotMetaManager extends LocalMetaManager {

	@Override
	public String getPrefix(UUID uuid) {

		Optional<Chat> opVault = LocalSpigotVaultHook.getInstance().getHook();

		if (opVault.isPresent()) {

			Chat vaultChat = opVault.get();

			// LEGACY HACK
			if (MultiChatLocal.getInstance().getDataStore().isLegacy()) {
				return MultiChatUtil.approximateHexCodes(vaultChat.getPlayerPrefix(Bukkit.getServer().getPlayer(uuid)));
			}

			return MultiChatUtil.reformatRGB(vaultChat.getPlayerPrefix(Bukkit.getServer().getPlayer(uuid)));

		}

		return "";

	}

	@Override
	public String getSuffix(UUID uuid) {

		Optional<Chat> opVault = LocalSpigotVaultHook.getInstance().getHook();

		if (opVault.isPresent()) {

			Chat vaultChat = opVault.get();

			// LEGACY HACK
			if (MultiChatLocal.getInstance().getDataStore().isLegacy()) {
				return MultiChatUtil.approximateHexCodes(vaultChat.getPlayerSuffix(Bukkit.getServer().getPlayer(uuid)));
			}

			return MultiChatUtil.reformatRGB(vaultChat.getPlayerSuffix(Bukkit.getServer().getPlayer(uuid)));

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

		Player player = Bukkit.getPlayer(uuid);

		if (player == null) return "";

		// If MultiChat is setting the display name...
		if (MultiChatLocal.getInstance().getDataStore().isSetDisplayName()) {

			String displayNameFormat = MultiChatLocal.getInstance().getDataStore().getDisplayNameFormatLastVal();

			// TODO This stuff could be refactored as it is duplicated between Spigot and Sponge
			displayNameFormat = displayNameFormat.replaceAll("%NICK%", getNick(uuid));
			displayNameFormat = displayNameFormat.replaceAll("%NAME%", player.getName());
			displayNameFormat = displayNameFormat.replaceAll("%PREFIX%", getPrefix(uuid));
			displayNameFormat = displayNameFormat.replaceAll("%SUFFIX%", getSuffix(uuid));
			displayNameFormat = MultiChatUtil.reformatRGB(displayNameFormat);
			displayNameFormat = displayNameFormat.replaceAll("&(?=[a-f,0-9,k-o,r,x])", "§");

			// LEGACY HACK
			if (MultiChatLocal.getInstance().getDataStore().isLegacy()) {
				displayNameFormat = MultiChatUtil.approximateHexCodes(displayNameFormat);
			}

			player.setDisplayName(displayNameFormat);
			player.setPlayerListName(displayNameFormat);

		}

		return player.getDisplayName();
	}

}
