package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.impl.SimpleMutableMessageChannel;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public class MultiChatMessageChannel extends SimpleMutableMessageChannel {

	private String channel;

	public String getMultiChatChannelName() {
		return this.channel;
	}

	public MultiChatMessageChannel(MultiChatLocalPlayer sender, Collection<MessageReceiver> originalRecipients) {

		Set<MessageReceiver> recipients = new HashSet<MessageReceiver>(originalRecipients);

		Iterator<MessageReceiver> it = recipients.iterator();

		while (it.hasNext()) {

			MessageReceiver p = it.next();

			if (p instanceof Player) {

				if (!((Player) p).getUniqueId().equals(sender.getUniqueId())) it.remove(); 

			}

		}

		/*LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		logger.debug("Creating new MultiChatMessageChannel for " + sender.getName());

		logger.debug("Before starting, this channel has " + getMembers().size() + " members!");

		//Set<Player> onlinePlayers = new HashSet<Player>(Sponge.getServer().getOnlinePlayers());

		logger.debug("How many recipients already? ... " + recipients.size());

		Iterator<MessageReceiver> it = recipients.iterator();

		this.channel = MultiChatLocal.getInstance().getChatManager().peekAtChatChannel(sender);
		Optional<LocalPseudoChannel> opChannelObject = MultiChatLocal.getInstance().getChatManager().getChannelObject(channel);

		logger.debug("Channel is : " + channel);

		if (opChannelObject.isPresent()) {

			logger.debug("We have an object for that channel which is good!");

			Set<UUID> ignoredPlayers;
			LocalPseudoChannel channelObject = opChannelObject.get();

			while (it.hasNext()) {

				MessageReceiver p = it.next();

				if (p instanceof Player) {

					Player p2 = (Player)p;

					Map<UUID, Set<UUID>> ignoreMap = MultiChatLocal.getInstance().getDataStore().getIgnoreMap();
					synchronized (ignoreMap) {
						ignoredPlayers = ignoreMap.get(p2.getUniqueId());

						if (ignoredPlayers == null) {
							logger.debug("...Their ignore map was null. They don't ignore anyone");
						} else {
							logger.debug("...They ignore " + ignoredPlayers.size() + " players.");
						}

						if ( (channelObject.whitelistMembers && channelObject.members.contains(p2.getUniqueId()))
								|| (!channelObject.whitelistMembers && !channelObject.members.contains(p2.getUniqueId()))) {

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

			}

		}*/

		for (MessageReceiver p : recipients) {
			//logger.debug("...Adding player " + p.getName() + " to recipients list...");
			addMember(p);
		}
		addMember(Sponge.getServer().getConsole());

	}

}
