package xyz.olivermartin.multichat.spigotbridge.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;

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

	@SuppressWarnings({ "unchecked" })
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

			// Handles cast messages sent from bungee which need to go to local chat stream

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String message = in.readUTF();
				Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			}

		} else if (channel.equals("multichat:channel")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			//DataInputStream in = new DataInputStream(stream);

			try {

				ObjectInputStream oin = new ObjectInputStream(stream);


				String playername = oin.readUTF();
				Player bukkitPlayer;

				bukkitPlayer = Bukkit.getPlayer(playername);

				if (bukkitPlayer == null) {
					return;
				}

				synchronized (bukkitPlayer) {

					String channelName = oin.readUTF();
					MultiChatSpigot.playerChannels.put(bukkitPlayer, channelName);

					boolean colour = oin.readBoolean();
					MultiChatSpigot.colourMap.put(bukkitPlayer.getUniqueId(), colour);

					boolean whitelistMembers = oin.readBoolean();
					List<UUID> channelMembers = (List<UUID>) oin.readObject();

					PseudoChannel channelObject = new PseudoChannel(channelName, channelMembers, whitelistMembers);
					MultiChatSpigot.channelObjects.put(channelName, channelObject);

				}

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			} catch (ClassNotFoundException e) {

				Bukkit.getLogger().info("Could not read list of uuids from channel message");
				e.printStackTrace();
			}
		} else if (channel.equals("multichat:ignore")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			//DataInputStream in = new DataInputStream(stream);

			try {

				ObjectInputStream oin = new ObjectInputStream(stream);

				MultiChatSpigot.ignoreMap = (Map<UUID, Set<UUID>>) oin.readObject();

			} catch (IOException e) {

				Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
				e.printStackTrace();

			} catch (ClassNotFoundException e) {

				Bukkit.getLogger().info("Could not read list of uuids from channel message");
				e.printStackTrace();
			}
		}
	} 

}
