package xyz.olivermartin.multichat.local.sponge;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.LocalMetaManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;

public class LocalSpongeMetaManager extends LocalMetaManager {

	@Override
	public String getPrefix(UUID uuid) {

		Optional<Player> opPlayer = Sponge.getServer().getPlayer(uuid);

		if (opPlayer.isPresent()) {

			Player player = opPlayer.get();

			if (player.getOption("prefix").isPresent()) {
				return MultiChatUtil.approximateRGBColourCodes(MultiChatUtil.translateColourCodes(player.getOption("prefix").get()));
			} else {
				return "";
			}

		} else {
			return "";
		}

	}

	@Override
	public String getSuffix(UUID uuid) {

		Optional<Player> opPlayer = Sponge.getServer().getPlayer(uuid);

		if (opPlayer.isPresent()) {

			Player player = opPlayer.get();

			if (player.getOption("suffix").isPresent()) {
				return MultiChatUtil.approximateRGBColourCodes(MultiChatUtil.translateColourCodes(player.getOption("suffix").get()));
			} else {
				return "";
			}

		} else {
			return "";
		}

	}

	@Override
	public String getWorld(UUID uuid) {

		Optional<Player> opPlayer = Sponge.getServer().getPlayer(uuid);

		if (opPlayer.isPresent()) {

			Player player = opPlayer.get();

			return player.getWorld().getName();

		} else {
			return "";
		}

	}

	@Override
	public String getDisplayName(UUID uuid) {

		Optional<Player> opPlayer = Sponge.getServer().getPlayer(uuid);

		if (!opPlayer.isPresent()) return "";

		Player player = opPlayer.get();

		// If MultiChat is setting the display name...
		if (MultiChatLocal.getInstance().getDataStore().isSetDisplayName()) {

			String displayNameFormat = MultiChatLocal.getInstance().getDataStore().getDisplayNameFormatLastVal();

			// TODO This stuff could be refactored as it is duplicated between Spigot and Sponge
			displayNameFormat = displayNameFormat.replaceAll("%NICK%", getNick(uuid));
			displayNameFormat = displayNameFormat.replaceAll("%NAME%", player.getName());
			displayNameFormat = displayNameFormat.replaceAll("%PREFIX%", getPrefix(uuid));
			displayNameFormat = displayNameFormat.replaceAll("%SUFFIX%", getSuffix(uuid));
			displayNameFormat = MultiChatUtil.translateColourCodes(displayNameFormat);
			displayNameFormat = MultiChatUtil.approximateRGBColourCodes(displayNameFormat);

			// TODO Sponge doesn't seem to like this... So we tend to work around it by sending back our original string
			player.offer(Keys.DISPLAY_NAME,Text.of(displayNameFormat));

			return displayNameFormat;

		}

		// TODO As Sponge doesn't like display names very much, MultiChat tends to use the GetName just in case...
		return player.getName();

	}

}
