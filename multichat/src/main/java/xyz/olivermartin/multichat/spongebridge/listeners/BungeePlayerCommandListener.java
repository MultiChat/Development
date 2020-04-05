package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

/**
 * Used to execute player specific commands sent from MultiChat on bungeecord
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class BungeePlayerCommandListener implements RawDataListener {

	public BungeePlayerCommandListener() {
		super();
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {

		String playerRegex = data.getUTF(0);
		String command = data.getUTF(1);

		/* THIS BIT NOW IS A BIT OF A HACK! */

		/*
		 * To implement sending single messages to global or local chat channels, we do a nice hack...
		 * We send a "command" of local or global chat...
		 * Then we deal with it here and add to the "CHAT QUEUE" in the MultiChatSponge class
		 * And then we send the chat message as if it were normal, but it will check the CHAT QUEUE to deal with it...
		 */

		if (command.startsWith("!SINGLE L MESSAGE!") || command.startsWith("!SINGLE G MESSAGE!")) {

			String message = command.substring("!SINGLE X MESSAGE!".length(),command.length());

			if (MultiChatSponge.chatQueues.containsKey(playerRegex.toLowerCase())) {

				Queue<String> chatQueue = MultiChatSponge.chatQueues.get(playerRegex.toLowerCase());
				chatQueue.add(command);

			} else {

				Queue<String> chatQueue = new LinkedList<String>();
				chatQueue.add(command);
				MultiChatSponge.chatQueues.put(playerRegex.toLowerCase(), chatQueue);

			}

			Optional<Player> opPlayer = Sponge.getServer().getPlayer(playerRegex);
			if (opPlayer.isPresent()) {
				EventContext context = EventContext.builder()
						.add(EventContextKeys.PLAYER_SIMULATED, opPlayer.get().getProfile())
						.add(EventContextKeys.PLUGIN, Sponge.getPluginManager().getPlugin("multichat").get())
						.build();
				opPlayer.get().simulateChat(Text.of(message), Cause.builder().append(opPlayer.get()).append(Sponge.getPluginManager().getPlugin("multichat").get()).build(context));
			}

			return;

		}

		/* END HACK */

		for (Player p : Sponge.getServer().getOnlinePlayers()) {

			if (p.getName().matches(playerRegex)) {

				Sponge.getCommandManager().process(p, command);
				System.out.println("!!!!" + command);

			}

		}

	}
}
