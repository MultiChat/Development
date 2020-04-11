package xyz.olivermartin.multichat.local.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.olivermartin.multichat.local.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

public class LocalSpongeConsoleLogger extends LocalConsoleLogger {

	protected LocalSpongeConsoleLogger() {
		super(MultiChatLocalPlatform.SPONGE);
	}

	@Override
	protected void displayMessageUsingLogger(String message) {
		System.out.println(message);
	}

	@Override
	protected void sendColouredMessageToConsoleSender(String message) {
		Sponge.getServer().getConsole().sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message));
	}

}
