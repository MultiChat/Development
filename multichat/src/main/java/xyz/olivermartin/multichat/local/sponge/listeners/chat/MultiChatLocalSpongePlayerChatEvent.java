package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.chat.MultiChatLocalPlayerChatEvent;

public class MultiChatLocalSpongePlayerChatEvent implements MultiChatLocalPlayerChatEvent {

	private MessageChannelEvent.Chat event;
	private MultiChatLocalPlayer player;

	public MultiChatLocalSpongePlayerChatEvent(MessageChannelEvent.Chat event, MultiChatLocalPlayer player) {
		this.event = event;
		this.player = player;
	}

	@Override
	public MultiChatLocalPlayer getPlayer() {
		return this.getPlayer();
	}

	@Override
	public String getMessage() {
		return TextSerializers.formattingCode('§').serialize(event.getFormatter().getBody().toText());
	}

	@Override
	public String getFormat() {
		return TextSerializers.formattingCode('§').serialize(event.getFormatter().getHeader().toText());
	}

	@Override
	public void setMessage(String message) {
		event.getFormatter().setBody(TextSerializers.formattingCode('§').deserialize(message));
	}

	@Override
	public void setFormat(String format) {
		event.getFormatter().setHeader(TextSerializers.formattingCode('§').deserialize(format));
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancelled) {
		event.setCancelled(cancelled);
	}

	@Override
	public void removeIgnoredPlayersAndNonChannelMembersFromRecipients(LocalPseudoChannel channel) {
		channel = null; // TODO Ignored for Sponge...
		Optional<MessageChannel> currentChannel = event.getChannel();
		Collection<MessageReceiver> recipients;
		if (currentChannel.isPresent()) {
			recipients = currentChannel.get().getMembers();
		} else {
			recipients = new HashSet<MessageReceiver>(Sponge.getServer().getOnlinePlayers());
		}
		MultiChatMessageChannel messageChannel = new MultiChatMessageChannel(player, recipients);
		event.setChannel(messageChannel);
	}

}
