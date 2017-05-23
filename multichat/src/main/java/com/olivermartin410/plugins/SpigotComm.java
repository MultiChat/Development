package com.olivermartin410.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.chat.Chat;

public class SpigotComm
extends JavaPlugin
implements PluginMessageListener, Listener
{

	public static Map<UUID,String> nicknames = new HashMap<UUID,String>();
	public static Chat chat = null;
	public static boolean vault;
	public static File configDir;

	public void onEnable()
	{
		configDir = getDataFolder();
		if (!getDataFolder().exists())
		{
			System.out.println("[MultiChatBridge] Creating plugin directory!");
			getDataFolder().mkdirs();
			configDir = getDataFolder();
		}

		File f = new File(configDir, "Nicknames.dat");
		if ((f.exists()) && (!f.isDirectory()))
		{
			System.out.println("[MultiChatBridge] Attempting startup load for Nicknames");
			nicknames = loadNicknames();
			System.out.println("[MultiChatBridge] Load completed!");
		}
		else
		{
			System.out.println("[MultiChatBridge] Some nicknames files do not exist to load. Must be first startup!");
			System.out.println("[MultiChatBridge] Enabling Nicknames! :D");
			System.out.println("[MultiChatBridge] Attempting to create hash files!");
			saveNicknames();
			System.out.println("[MultiChatBridge] The files were created!");
		}
		getServer().getMessenger().registerOutgoingPluginChannel(this, "MultiChat");
		getServer().getMessenger().registerIncomingPluginChannel(this, "MultiChat", this);
		getServer().getPluginManager().registerEvents(this, this);
		vault = setupChat();
		if (vault) {
			System.out.println("MultiChat has successfully connected to vault!");
		}
	}

	private boolean setupChat()
	{
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	public void onDisable() {
		saveNicknames();
	}

	public void sendMessage(String message, String playername)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try
		{
			out.writeUTF(message);
			out.writeUTF(playername);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		((PluginMessageRecipient)getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(this, "MultiChat", stream.toByteArray());
	}
	
	private void updatePlayerDisplayName(String playername) {
		
		String nickname;
		if (nicknames.containsKey(Bukkit.getPlayer(playername).getUniqueId())) {
			nickname = nicknames.get(Bukkit.getPlayer(playername).getUniqueId());
		} else {
			nickname =  Bukkit.getPlayer(playername).getName();
		}
		if (vault) {
			sendMessage(chat.getPlayerPrefix(Bukkit.getPlayer(playername)) + nickname + chat.getPlayerSuffix(Bukkit.getPlayer(playername)), playername);
			Bukkit.getPlayer(playername).setDisplayName((chat.getPlayerPrefix(Bukkit.getPlayer(playername)) + nickname + chat.getPlayerSuffix(Bukkit.getPlayer(playername))).replaceAll("&(?=[a-f,0-9,k-o,r])", "§"));
			Bukkit.getPlayer(playername).setPlayerListName((chat.getPlayerPrefix(Bukkit.getPlayer(playername)) + nickname + chat.getPlayerSuffix(Bukkit.getPlayer(playername))).replaceAll("&(?=[a-f,0-9,k-o,r])", "§"));
		} else {
			sendMessage(nickname, playername);
			Bukkit.getPlayer(playername).setDisplayName((nickname).replaceAll("&(?=[a-f,0-9,k-o,r])", "§"));
			Bukkit.getPlayer(playername).setPlayerListName((nickname).replaceAll("&(?=[a-f,0-9,k-o,r])", "§"));
		}
		
	}

	public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
	{
		if (channel.equals("MultiChat"))
		{
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);
			try
			{
				String playername = in.readUTF();

				synchronized (Bukkit.getPlayer(playername)) {
					if (Bukkit.getPlayer(playername) == null) {
						return;
					}
					
					updatePlayerDisplayName(playername);
					
				}
			}
			catch (IOException e)
			{
				System.out.println("[MultiChatBridge] Failed to contact bungeecord");

				e.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onLogin(final PlayerJoinEvent event)
	{
		new BukkitRunnable()
		{
			public void run()
			{
				synchronized (event.getPlayer()) {
					if (event.getPlayer() == null) {
						return;
					}
					String playername = event.getPlayer().getName();
					updatePlayerDisplayName(playername);
					
				}
			}
		}

		.runTaskLater(this, 10L);
	}

	private void addNickname(UUID uuid, String nickname) {
		nicknames.put(uuid,nickname);
	}

	private void removeNickname(UUID uuid) {
		nicknames.remove(uuid);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("nick")) {

			Player sender;

			if (commandSender instanceof Player) {
				sender = (Player) commandSender;
			} else {
				commandSender.sendMessage(ChatColor.DARK_RED + "Only players can use this command!");
				return true;
			}

			if (args.length != 2) {
				// When onCommand() returns false, the help message associated with that command is displayed.
				return false;
			}

			Player target = sender.getServer().getPlayer(args[0]);
			// Make sure the player is online.
			if (target == null) {
				sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not currently online so cannot be nicknamed!");
				return true;
			}

			if (target != sender) {
				if (!sender.hasPermission("multichatbridge.nick.others")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to nickname other players!");
					return true;
				}
			}

			UUID targetUUID = target.getUniqueId();

			if (args[1].equalsIgnoreCase("off")) {
				removeNickname(targetUUID);
				updatePlayerDisplayName(target.getName());
				sender.sendMessage(ChatColor.GREEN + args[0] + " has had their nickname removed!");
				return true;
			}

			addNickname(targetUUID,args[1]);
			updatePlayerDisplayName(target.getName());

			sender.sendMessage(ChatColor.GREEN + args[0] + " has been nicknamed!");

			return true;
		}
		return false;
	}

	public static void saveNicknames()
	{
		try
		{
			File file = new File(configDir, "Nicknames.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(nicknames);
			out.close();
			System.out.println("[MultiChatBridge] The nicknames file was successfully saved!");
		}
		catch (IOException e)
		{
			System.out.println("[MultiChatBridge] An error has occured writing the nicknames file!");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static HashMap<UUID, String> loadNicknames()
	{
		HashMap<UUID, String> result = null;
		try
		{
			File file = new File(configDir, "Nicknames.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<UUID, String>)in.readObject();
			in.close();
			System.out.println("[MultiChatBridge] The nicknames file was successfully loaded!");
		}
		catch (IOException|ClassNotFoundException e)
		{
			System.out.println("[MultiChatBridge] An error has occured reading the nicknames file!");
			e.printStackTrace();
		}
		return result;
	}

}



