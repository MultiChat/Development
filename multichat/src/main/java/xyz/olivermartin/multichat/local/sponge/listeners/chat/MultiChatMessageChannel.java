package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.impl.SimpleMutableMessageChannel;

import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public class MultiChatMessageChannel extends SimpleMutableMessageChannel {

	private String channel;

	public String getMultiChatChannelName() {
		return this.channel;
	}

	public MultiChatMessageChannel(MultiChatLocalPlayer sender) {

		LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		logger.debug("Creating new MultiChatMessageChannel for " + sender.getName());

		logger.debug("Before starting, this channel has " + getMembers().size() + " members!");

		Set<Player> onlinePlayers = new HashSet<Player>(Sponge.getServer().getOnlinePlayers());

		logger.debug("How many online players? ... " + onlinePlayers.size());

		Iterator<Player> it = onlinePlayers.iterator();

		this.channel = MultiChatLocal.getInstance().getChatManager().pollChatChannel(sender); // TODO Should this be poll?
		Optional<LocalPseudoChannel> opChannelObject = MultiChatLocal.getInstance().getChatManager().getChannelObject(channel);

		logger.debug("Channel is : " + channel);

		if (opChannelObject.isPresent()) {

			logger.debug("We have an object for that channel which is good!");

			Set<UUID> ignoredPlayers;
			LocalPseudoChannel channelObject = opChannelObject.get();

			while (it.hasNext()) {

				Player p = it.next();

				logger.debug("...Analysing player : " + p.getName());

				ignoredPlayers = MultiChatLocal.getInstance().getDataStore().ignoreMap.get(p.getUniqueId());

				if (ignoredPlayers == null) {
					logger.debug("...Their ignore map was null. They don't ignore anyone");
				} else {
					logger.debug("...They ignore " + ignoredPlayers.size() + " players.");
				}

				if ( (channelObject.whitelistMembers && channelObject.members.contains(p.getUniqueId()))
						|| (!channelObject.whitelistMembers && !channelObject.members.contains(p.getUniqueId()))) {

					logger.debug("...They are part of this pseudochannel object!");

					if (ignoredPlayers != null) {
						if (ignoredPlayers.contains(sender.getUniqueId())) {
							logger.debug("...Do they ignore the sender (" + sender.getName() + ")? --> YES");
							it.remove();
						}
					}
				} else {
					it.remove();
				}
			}

		}

		for (Player p : onlinePlayers) {
			logger.debug("...Adding player " + p.getName() + " to recipients list...");
			addMember(p);
		}
		addMember(Sponge.getServer().getConsole());

	}

}
