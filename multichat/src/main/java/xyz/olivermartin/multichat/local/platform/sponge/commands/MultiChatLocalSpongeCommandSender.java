package xyz.olivermartin.multichat.local.platform.sponge.commands;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;

public class MultiChatLocalSpongeCommandSender implements MultiChatLocalCommandSender {

	private CommandSource sender;

	public MultiChatLocalSpongeCommandSender(CommandSource sender) {
		this.sender = sender;
	}

	@Override
	public void sendGoodMessage(String message) {
		sender.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&a" + message));
	}

	@Override
	public void sendBadMessage(String message) {
		sender.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&c" + message));
	}

	@Override
	public void sendInfoMessageA(String message) {
		sender.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&3" + message));
	}

	@Override
	public void sendInfoMessageB(String message) {
		sender.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&b" + message));
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public boolean isPlayer() {
		return (sender instanceof Player);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

}
