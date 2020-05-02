package xyz.olivermartin.multichat.local.spigot;

import java.util.UUID;

import org.bukkit.entity.Player;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.spigot.commands.MultiChatLocalSpigotCommandSender;

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
