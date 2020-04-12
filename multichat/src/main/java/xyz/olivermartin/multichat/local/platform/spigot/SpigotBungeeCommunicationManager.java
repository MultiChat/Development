package xyz.olivermartin.multichat.local.platform.spigot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.communication.LocalBungeeCommunicationManager;

/**
 * Allows MultiChatLocal running on Spigot to communicate with a Bungeecord Proxy
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SpigotBungeeCommunicationManager extends LocalBungeeCommunicationManager {

	public SpigotBungeeCommunicationManager() {
		super(MultiChatLocalPlatform.SPIGOT);
	}

	protected boolean sendUUIDAndString(String channel, UUID uuid, String value) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(uuid.toString());
			out.writeUTF(value);

		} catch (IOException e) {

			return false;

		}

		if (Bukkit.getServer().getOnlinePlayers().size() < 1) return false;

		((PluginMessageRecipient)Bukkit.getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), channel, stream.toByteArray());

		return true;

	}

}
