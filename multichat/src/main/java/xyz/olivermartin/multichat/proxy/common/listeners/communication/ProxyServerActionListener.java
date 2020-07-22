package xyz.olivermartin.multichat.proxy.common.listeners.communication;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.common.communication.CommChannels;

/**
 * Listener for communication over the Server Action communication channel
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyServerActionListener implements Listener {

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent event) {

		// Ignore if sent to a different channel
		if (!event.getTag().equals(CommChannels.SERVER_ACTION)) return;

		event.setCancelled(true);

		ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
		DataInputStream in = new DataInputStream(stream);

		String command;

		try {
			command = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);

	}

}
