package xyz.olivermartin.multichat.local.sponge;

import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.sponge.commands.MultiChatLocalSpongeCommandSender;

public class MultiChatLocalSpongePlayer extends MultiChatLocalSpongeCommandSender implements MultiChatLocalPlayer {

	private Player player;

	public MultiChatLocalSpongePlayer(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

}
