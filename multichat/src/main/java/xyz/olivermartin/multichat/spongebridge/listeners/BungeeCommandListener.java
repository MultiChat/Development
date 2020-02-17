package xyz.olivermartin.multichat.spongebridge.listeners;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.scheduler.Task;

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

		Task.Builder taskBuilder = Task.builder();
		final String command = data.getUTF(0);
		
		taskBuilder.execute(new Runnable() {
		    public void run() {
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
		    }
		});
		
		taskBuilder.delayTicks(1);
		
		taskBuilder.submit(Sponge.getPluginManager().getPlugin("multichat").get().getInstance().get());

	}
}
