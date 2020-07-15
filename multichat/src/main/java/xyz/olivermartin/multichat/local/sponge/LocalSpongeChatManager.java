package xyz.olivermartin.multichat.local.sponge;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.rojo8399.placeholderapi.PlaceholderService;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.sponge.hooks.LocalSpongePAPIHook;

public class LocalSpongeChatManager extends LocalChatManager {

	@Override
	public String translateColourCodes(String message, boolean rgb) {

		if (rgb) {
			message = MultiChatLocal.getInstance().getChatManager().reformatRGB(message);
			message = message.replaceAll("&(?=[a-f,0-9,k-o,r,x])", "§");
			message = MultiChatUtil.approximateHexCodes(message);
			return TextSerializers.formattingCode('§').serialize(TextSerializers.FORMATTING_CODE.deserialize(message));
		} else {
			return TextSerializers.formattingCode('§').serialize(TextSerializers.FORMATTING_CODE.deserialize(message));
		}

	}

	@Override
	public String processExternalPlaceholders(MultiChatLocalPlayer player, String message) {

		// If we are hooked with PAPI then use their placeholders!
		if (LocalSpongePAPIHook.getInstance().isHooked()) {
			PlaceholderService papi = LocalSpongePAPIHook.getInstance().getHook().get();
			Optional<Player> opPlayer = Sponge.getServer().getPlayer(player.getUniqueId());
			if (opPlayer.isPresent()) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("Going into PAPI we have: " + message);
				MultiChatLocal.getInstance().getConsoleLogger().debug("Going into PAPI we have (visualised): " + message.replace("&", "(#d)").replace("§", "(#e)"));

				message = TextSerializers.FORMATTING_CODE.serialize(papi.replaceSourcePlaceholders(message+"#", opPlayer.get()));

				MultiChatLocal.getInstance().getConsoleLogger().debug("Serialised we have: " + message);
				MultiChatLocal.getInstance().getConsoleLogger().debug("Serialised we have (visualised): " + message.replace("&", "(#d)").replace("§", "(#e)"));

				// PAPI replaces unknown placeholders with {key}, so change them back to %key%!!
				message = message.substring(0,message.length()-1);
				message = message.replace("{NAME}", "%NAME%");
				message = message.replace("{DISPLAYNAME}", "%DISPLAYNAME%");
				message = message.replace("{PREFIX}", "%PREFIX%");
				message = message.replace("{SUFFIX}", "%SUFFIX%");
				message = message.replace("{NICK}", "%NICK%");
				message = message.replace("{SERVER}", "%SERVER%");
				message = message.replace("{WORLD}", "%WORLD%");
				message = message.replace("{MODE}", "%MODE%");

				MultiChatLocal.getInstance().getConsoleLogger().debug("After PAPI we have: " + message);
				MultiChatLocal.getInstance().getConsoleLogger().debug("After PAPI we have (visualised): " + message.replace("&", "(#d)").replace("§", "(#e)"));
			}
		}

		return message;

	}

}
