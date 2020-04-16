package xyz.olivermartin.multichat.local.platform.sponge.listeners.chat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.impl.SimpleMutableMessageChannel;

import xyz.olivermartin.multichat.local.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class MultiChatMessageChannel extends SimpleMutableMessageChannel {
	
	private String channel;
	
	public String getMultiChatChannelName() {
		return this.channel;
	}

	public MultiChatMessageChannel(MultiChatLocalPlayer sender) {

		Set<Player> onlinePlayers = new HashSet<Player>(Sponge.getServer().getOnlinePlayers());
		Iterator<Player> it = onlinePlayers.iterator();

		this.channel = MultiChatLocal.getInstance().getChatManager().pollChatChannel(sender); // TODO Should this be poll?
		Optional<LocalPseudoChannel> opChannelObject = MultiChatLocal.getInstance().getChatManager().getChannelObject(channel);

		if (opChannelObject.isPresent()) {

			Set<UUID> ignoredPlayers;
			LocalPseudoChannel channelObject = opChannelObject.get();

			while (it.hasNext()) {

				Player p = it.next();

				ignoredPlayers = MultiChatSponge.ignoreMap.get(p.getUniqueId());

				if ( (channelObject.whitelistMembers && channelObject.members.contains(p.getUniqueId()))
						|| (!channelObject.whitelistMembers && !channelObject.members.contains(p.getUniqueId()))) {
					if (ignoredPlayers != null) {
						if (ignoredPlayers.contains(sender.getUniqueId())) {
							it.remove();
						}
					}
				} else {
					it.remove();
				}
			}

		}

		for (Player p : onlinePlayers) {
			addMember(p);
		}
		addMember(Sponge.getServer().getConsole());

	}

}
