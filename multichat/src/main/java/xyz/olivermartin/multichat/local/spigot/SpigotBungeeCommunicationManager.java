package xyz.olivermartin.multichat.local.spigot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import xyz.olivermartin.multichat.local.common.LocalBungeeCommunicationManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;

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

	@Override
	protected boolean sendStringAndString(String channel, String string1, String string2) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(string1);
			out.writeUTF(string2);

		} catch (IOException e) {

			return false;

		}

		if (Bukkit.getServer().getOnlinePlayers().size() < 1) return false;

		((PluginMessageRecipient)Bukkit.getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), channel, stream.toByteArray());

		return true;
	}

	@Override
	protected boolean sendString(String channel, String string) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(string);

		} catch (IOException e) {

			return false;

		}

		if (Bukkit.getServer().getOnlinePlayers().size() < 1) return false;

		((PluginMessageRecipient)Bukkit.getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), channel, stream.toByteArray());

		return true;

	}

	@Override
	protected boolean sendUUIDAndStringAndString(String channel, UUID uuid, String value1, String value2) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(uuid.toString());
			out.writeUTF(value1);
			out.writeUTF(value2);

		} catch (IOException e) {

			return false;

		}

		if (Bukkit.getServer().getOnlinePlayers().size() < 1) return false;

		((PluginMessageRecipient)Bukkit.getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), channel, stream.toByteArray());

		return true;

	}

	@Override
	protected boolean sendUUIDAndStringAndStringAndString(String channel, UUID uuid, String value1, String value2,
			String value3) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(uuid.toString());
			out.writeUTF(value1);
			out.writeUTF(value2);
			out.writeUTF(value3);

		} catch (IOException e) {

			return false;

		}

		if (Bukkit.getServer().getOnlinePlayers().size() < 1) return false;

		((PluginMessageRecipient)Bukkit.getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), channel, stream.toByteArray());

		return true;

	}

	@Override
	protected boolean sendPlatformChatMessage(String channel, UUID uuid, String chatChannel, String message, String format, Set<UUID> otherRecipients) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			
			ObjectOutputStream out = new ObjectOutputStream(stream);

			out.writeUTF(uuid.toString());
			out.writeUTF(chatChannel);
			out.writeUTF(message);
			out.writeUTF(format);
			out.writeObject(otherRecipients);
			out.flush();

		} catch (IOException e) {

			return false;

		}

		if (Bukkit.getServer().getOnlinePlayers().size() < 1) return false;

		((PluginMessageRecipient)Bukkit.getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), channel, stream.toByteArray());

		return true;


	}

}
