package xyz.olivermartin.multichat.spongebridge.listeners;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

/**
 * Used to execute command send from MultiChat on bungeecord
 * I.e. for anti spam and regex actions
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class BungeeCommandListener implements RawDataListener {

	public BungeeCommandListener() {
		super();
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {

		String command = data.getUTF(0);
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);

	}
}
