package xyz.olivermartin.multichat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeComm
implements Listener
{

	public static void sendMessage(String message, ServerInfo server)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try
		{
			out.writeUTF(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		server.sendData("multichat:comm", stream.toByteArray());

	}

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent ev)
	{
		if (!ev.getTag().equals("multichat:comm")) {
			return;
		}
		if (!(ev.getSender() instanceof Server)) {
			return;
		}
		if (ev.getTag().equals("multichat:comm"))
		{
			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);
			try
			{
				String playerdisplayname = in.readUTF();
				String playername = in.readUTF();

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playername);
				synchronized (player) {
					if (MultiChat.configman.config.getBoolean("fetch_spigot_display_names") == true && player != null) {
						//player.setDisplayName(playerdisplayname.replaceAll("&", "§"));
						player.setDisplayName(playerdisplayname.replaceAll("&(?=[a-f,0-9,k-o,r])", "§"));
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
