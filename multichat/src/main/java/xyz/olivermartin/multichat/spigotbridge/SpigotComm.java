package xyz.olivermartin.multichat.spigotbridge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Optional;
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

/**
 * The MAIN MultiChatBridge Class - SPIGOT COMM
 * <p>Handles communication with BungeeCord from the SPIGOT side, also controls /nick etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SpigotComm extends JavaPlugin implements PluginMessageListener, Listener {

	public static Chat chat = null;
	public static boolean vault;

	public static File configDir;

	private static final String nameDataFile = "namedata.dat";
	private static File legacyNicknameFile; 

	@SuppressWarnings("unchecked")
	public void onEnable() {

		configDir = getDataFolder();
		legacyNicknameFile = new File(configDir, "Nicknames.dat");
		
		if (!getDataFolder().exists()) {
			System.out.println("[MultiChat] [BRIDGE] Creating plugin directory!");
			getDataFolder().mkdirs();
			configDir = getDataFolder();
		}

		File f = new File(configDir, nameDataFile);

		if ((f.exists()) && (!f.isDirectory())) {

			System.out.println("[MultiChat] [BRIDGE] Attempting startup load for Nicknames");

			File file = new File(configDir, nameDataFile);
			FileInputStream saveFile;
			try {
				saveFile = new FileInputStream(file);
				NameManager.getInstance().loadNicknameData(saveFile);
			} catch (FileNotFoundException e) {
				System.out.println("[MultiChat] [BRIDGE] [ERROR] Could not load nickname data");
				e.printStackTrace();
			}

			System.out.println("[MultiChat] [BRIDGE] Load completed!");

		} else if (legacyNicknameFile.exists()) {

			// LEGACY NICKNAME FILE HANDLING --------------------------------------------

			HashMap<UUID, String> result = null;

			try {

				FileInputStream saveFile = new FileInputStream(legacyNicknameFile);
				ObjectInputStream in = new ObjectInputStream(saveFile);
				result = (HashMap<UUID, String>) in.readObject();
				in.close();
				System.out.println("[MultiChat] [BRIDGE] Loaded a legacy (pre 1.6) nicknames file. Attempting conversion...");

				int counter = 0;

				for (UUID u : result.keySet()) {

					counter++;
					NameManager.getInstance().registerOfflinePlayerByUUID(u, "NotJoinedYet"+String.valueOf(counter));
					NameManager.getInstance().setNickname(u, result.get(u));

				}

				File file = new File(configDir, nameDataFile);
				FileOutputStream saveFile2;
				try {
					saveFile2 = new FileOutputStream(file);
					NameManager.getInstance().saveNicknameData(saveFile2);
				} catch (FileNotFoundException e) {
					System.out.println("[MultiChat] [BRIDGE] [ERROR] Could not save nickname data");
					e.printStackTrace();
				}

				System.out.println("[MultiChat] [BRIDGE] The files were created!");

			} catch (IOException|ClassNotFoundException e) {

				System.out.println("[MultiChat] [BRIDGE] An error has occured reading the legacy nicknames file. Please delete it.");
				e.printStackTrace();

			}

		} else {

			System.out.println("[MultiChat] [BRIDGE] Name data files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] [BRIDGE] Enabling Nicknames! :D");
			System.out.println("[MultiChat] [BRIDGE] Attempting to create hash files!");

			File file = new File(configDir, nameDataFile);
			FileOutputStream saveFile;
			try {
				saveFile = new FileOutputStream(file);
				NameManager.getInstance().saveNicknameData(saveFile);
			} catch (FileNotFoundException e) {
				System.out.println("[MultiChat] [BRIDGE] [ERROR] Could not save nickname data");
				e.printStackTrace();
			}

			System.out.println("[MultiChat] [BRIDGE] The files were created!");

		}

		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:comm");
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:comm", this);

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(NameManager.getInstance(), this);

		vault = setupChat();

		if (vault) {
			System.out.println("MultiChat has successfully connected to vault!");
		}
	}

	private boolean setupChat() {

		if (getServer().getPluginManager().getPlugin("Vault") == null) {

			return false;

		}

		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;

	}

	public void onDisable() {

		File file = new File(configDir, nameDataFile);
		FileOutputStream saveFile;
		try {
			saveFile = new FileOutputStream(file);
			NameManager.getInstance().saveNicknameData(saveFile);
		} catch (FileNotFoundException e) {
			System.out.println("[MultiChat] [BRIDGE] [ERROR] Could not save nickname data");
			e.printStackTrace();
		}
	}

	public void sendMessage(String message, String playername) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {
			out.writeUTF(message);
			out.writeUTF(playername);
		} catch (IOException e) {
			e.printStackTrace();
		}

		((PluginMessageRecipient)getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(this, "multichat:comm", stream.toByteArray());

	}

	private void updatePlayerDisplayName(String playername) {

		String nickname;

		nickname = NameManager.getInstance().getCurrentName(Bukkit.getPlayer(playername).getUniqueId());

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

	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {

		if (channel.equals("multichat:comm")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String playername = in.readUTF();

				synchronized (Bukkit.getPlayer(playername)) {

					if (Bukkit.getPlayer(playername) == null) {
						return;
					}
					updatePlayerDisplayName(playername);

				}

			} catch (IOException e) {

				System.out.println("[MultiChat] [BRIDGE] Failed to contact bungeecord");
				e.printStackTrace();

			}
		}
	}

	@EventHandler
	public void onLogin(final PlayerJoinEvent event) {

		new BukkitRunnable() {

			public void run() {

				synchronized (event.getPlayer()) {

					if (event.getPlayer() == null) {
						return;
					}
					String playername = event.getPlayer().getName();
					updatePlayerDisplayName(playername);

				}

			}

		}.runTaskLater(this, 10L);

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

			if (args.length < 1 || args.length > 2) {
				// When onCommand() returns false, the help message associated with that command is displayed.
				return false;
			}

			if (args.length == 1) {

				UUID targetUUID = sender.getUniqueId();

				if (args[0].equalsIgnoreCase("off")) {
					NameManager.getInstance().removeNickname(targetUUID);
					updatePlayerDisplayName(sender.getName());
					sender.sendMessage("You have had your nickname removed!");
					return true;
				}

				NameManager.getInstance().setNickname(targetUUID, args[0]);
				updatePlayerDisplayName(sender.getName());

				sender.sendMessage("You have been nicknamed!");

				return true;

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
				NameManager.getInstance().removeNickname(targetUUID);
				updatePlayerDisplayName(target.getName());
				sender.sendMessage(ChatColor.GREEN + args[0] + " has had their nickname removed!");
				return true;
			}

			NameManager.getInstance().setNickname(targetUUID, args[1]);
			updatePlayerDisplayName(target.getName());

			sender.sendMessage(ChatColor.GREEN + args[0] + " has been nicknamed!");

			return true;

		} else if (cmd.getName().equalsIgnoreCase("realname")) {

			Player sender;

			if (commandSender instanceof Player) {
				sender = (Player) commandSender;
			} else {
				commandSender.sendMessage(ChatColor.DARK_RED + "Only players can use this command!");
				return true;
			}

			if (args.length != 1) {
				// When onCommand() returns false, the help message associated with that command is displayed.
				return false;
			}

			if (NameManager.getInstance().existsNickname(args[0])) {

				Optional<String> player;
				player = NameManager.getInstance().getNameFromNickname(args[0]);

				if (player.isPresent()) {

					sender.sendMessage(ChatColor.GREEN + "Nickname: '" + args[0] + "' Belongs to player: '" + player.get() + "'");

				} else {

					sender.sendMessage(ChatColor.DARK_RED + "No one could be found with nickname: " + args[0]);

				}

				return true;

			} else {

				sender.sendMessage(ChatColor.DARK_RED + "No one could be found with nickname: " + args[0]);
				return true;

			}

		}
		return false;
	}

	//	public static void saveNicknames() {
	//
	//		try {
	//
	//			File file = new File(configDir, nameDataFile);
	//			FileOutputStream saveFile = new FileOutputStream(file);
	//			ObjectOutputStream out = new ObjectOutputStream(saveFile);
	//			out.writeObject(nicknames);
	//			out.close();
	//			System.out.println("[MultiChat] [BRIDGE] The nicknames file was successfully saved!");
	//
	//		} catch (IOException e) {
	//
	//			System.out.println("[MultiChat] [BRIDGE] An error has occured writing the nicknames file!");
	//			e.printStackTrace();
	//
	//		}
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	public static HashMap<UUID, String> loadNicknames() {
	//
	//		HashMap<UUID, String> result = null;
	//
	//		try {
	//
	//			File file = new File(configDir, nameDataFile);
	//			FileInputStream saveFile = new FileInputStream(file);
	//			ObjectInputStream in = new ObjectInputStream(saveFile);
	//			result = (HashMap<UUID, String>)in.readObject();
	//			in.close();
	//			System.out.println("[MultiChat] [BRIDGE] The nicknames file was successfully loaded!");
	//
	//		} catch (IOException|ClassNotFoundException e) {
	//
	//			System.out.println("[MultiChat] [BRIDGE] An error has occured reading the nicknames file!");
	//			e.printStackTrace();
	//
	//		}
	//
	//		return result;
	//
	//	}
}
