package xyz.olivermartin.multichat.spigotbridge.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.events.InducedAsyncPlayerChatEvent;

public class MultiChatPluginMessageListener implements PluginMessageListener {

	private static MultiChatPluginMessageListener instance;

	public static MultiChatPluginMessageListener getInstance() {
		return instance;
	}

	static {
		instance = new MultiChatPluginMessageListener();
	}

	/* --- END STATIC --- */

	private MultiChatPluginMessageListener() {
		/* Empty */
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {

		if (channel.equals("multichat:comm")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				boolean setDisplayName = false;
				boolean globalChat = false;
				String displayNameFormat = "";
				String playername = in.readUTF();
				Player bukkitPlayer;

				bukkitPlayer = Bukkit.getPlayer(playername);

				if (bukkitPlayer == null) {
					return;
				}

				synchronized (bukkitPlayer) {

					if (in.readUTF().equals("T")) {
						setDisplayName = true;
					}

					displayNameFormat = in.readUTF();

					MultiChatSpigot.setDisplayNameLastVal = setDisplayName;
					MultiChatSpigot.displayNameFormatLastVal = displayNameFormat;

					MetaManager.getInstance().updatePlayerMeta(playername, setDisplayName, displayNameFormat);

					if (in.readUTF().equals("T")) {
						globalChat = true;
					}

					MultiChatSpigot.globalChatServer = globalChat;

					MultiChatSpigot.globalChatFormat = in.readUTF();

				}

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			}
		} else if (channel.equals("multichat:action")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String command = in.readUTF();
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command); 

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			}
		} else if (channel.equals("multichat:paction")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String playerRegex = in.readUTF();
				String command = in.readUTF();

				for (Player p : Bukkit.getServer().getOnlinePlayers()) {

					if (p.getName().matches(playerRegex)) {
						Bukkit.getServer().dispatchCommand(p, command);
					}

				}

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			}
		} else if (channel.equals("multichat:chat")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String playername = in.readUTF();
				Player bukkitPlayer;

				bukkitPlayer = Bukkit.getPlayer(playername);

				if (bukkitPlayer == null) {
					return;
				}

				synchronized (bukkitPlayer) {

					String format = in.readUTF();
					String message = in.readUTF();

					boolean colour = in.readBoolean();

					String playerString = in.readUTF();

					Set<String> playerNames = new HashSet<String>(Arrays.asList(playerString.split(" ")));
					Set<Player> players = new HashSet<Player>(Bukkit.getOnlinePlayers());
					Iterator<Player> it = players.iterator();

					while(it.hasNext()) {
						if (!playerNames.contains(it.next().getName())) {
							it.remove();
						}
					}

					AsyncPlayerChatEvent event = new InducedAsyncPlayerChatEvent(false, bukkitPlayer, message, players);
					//TODO event.setFormat(format);

					Bukkit.getPluginManager().callEvent(event);

					String toSend;
					if (colour) {
						toSend = ChatColor.translateAlternateColorCodes('&', format.replace("%MESSAGE%", message));
					} else {
						toSend = ChatColor.translateAlternateColorCodes('&', format.replace("%MESSAGE%", "")) + message;
					}

					for (Player p : players) {
						p.sendMessage(toSend);
					}

				}

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			}
		} else if (channel.equals("multichat:channel")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String playername = in.readUTF();
				Player bukkitPlayer;

				bukkitPlayer = Bukkit.getPlayer(playername);

				if (bukkitPlayer == null) {
					return;
				}

				synchronized (bukkitPlayer) {

					String channelName = in.readUTF();
					MultiChatSpigot.playerChannels.put(bukkitPlayer, channelName);

				}

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			}
		}
	}

}
