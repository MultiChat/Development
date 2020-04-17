package xyz.olivermartin.multichat.local.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.rojo8399.placeholderapi.PlaceholderService;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.sponge.hooks.LocalSpongePAPIHook;

public class LocalSpongeChatManager extends LocalChatManager {

	@Override
	@Deprecated
	public String translateColourCodes(String message) {
		return TextSerializers.FORMATTING_CODE.deserialize(message).toString();
	}
	
	public Text translateColourCodesForSponge(String message) {
		return TextSerializers.FORMATTING_CODE.deserialize(message);
	}

	@Override
	public String processExternalPlaceholders(MultiChatLocalPlayer player, String message) {

		// If we are hooked with PAPI then use their placeholders!
		if (LocalSpongePAPIHook.getInstance().isHooked()) {
			PlaceholderService papi = LocalSpongePAPIHook.getInstance().getHook().get();
			message = TextSerializers.FORMATTING_CODE.serialize(papi.replaceSourcePlaceholders(message, Sponge.getServer().getPlayer(player.getUniqueId()))); // TODO Do not know if this will work!
		}

		return message;

	}

}
