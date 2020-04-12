package xyz.olivermartin.multichat.local.platform.spigot.commands;

import java.util.UUID;

import org.bukkit.entity.Player;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalPlayer;

public class MultiChatLocalSpigotPlayer extends MultiChatLocalSpigotCommandSender implements MultiChatLocalPlayer {

	private Player player;

	public MultiChatLocalSpigotPlayer(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}


}
