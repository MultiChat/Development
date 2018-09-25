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
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

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

	private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");

	private static boolean setDisplayNameLastVal = false;
	private static String displayNameFormatLastVal = "%PREFIX%%NICK%%SUFFIX%";

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

				if (result != null) {

					if (result.keySet() != null) {

						for (UUID u : result.keySet()) {

							counter++;
							NameManager.getInstance().registerOfflinePlayerByUUID(u, "NotJoinedYet"+String.valueOf(counter));
							NameManager.getInstance().setNickname(u, result.get(u));

						}

					}

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
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:prefix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:suffix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:nick");
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:comm", this);
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:action", this);
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:paction", this);

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

	public void sendPluginChannelMessage(String channel, UUID uuid, String message) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {
			out.writeUTF(uuid.toString());
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}

		((PluginMessageRecipient)getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(this, channel, stream.toByteArray());

	}

	private void updatePlayerMeta(String playername, boolean setDisplayName, String displayNameFormat) {

		String nickname;

		nickname = NameManager.getInstance().getCurrentName(Bukkit.getPlayer(playername).getUniqueId());

		sendPluginChannelMessage("multichat:nick", Bukkit.getPlayer(playername).getUniqueId(), nickname);

		if (vault) {

			sendPluginChannelMessage("multichat:prefix", Bukkit.getPlayer(playername).getUniqueId(), chat.getPlayerPrefix(Bukkit.getPlayer(playername)));
			sendPluginChannelMessage("multichat:suffix", Bukkit.getPlayer(playername).getUniqueId(), chat.getPlayerSuffix(Bukkit.getPlayer(playername)));

			if (setDisplayName) {

				displayNameFormat = displayNameFormat.replaceAll("%NICK%", nickname);
				displayNameFormat = displayNameFormat.replaceAll("%NAME%", playername);
				displayNameFormat = displayNameFormat.replaceAll("%PREFIX%", chat.getPlayerPrefix(Bukkit.getPlayer(playername)));
				displayNameFormat = displayNameFormat.replaceAll("%SUFFIX%", chat.getPlayerSuffix(Bukkit.getPlayer(playername)));
				displayNameFormat = displayNameFormat.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");

				Bukkit.getPlayer(playername).setDisplayName(displayNameFormat);
				Bukkit.getPlayer(playername).setPlayerListName(displayNameFormat);
			}
		} else {

			if (setDisplayName) {

				displayNameFormat = displayNameFormat.replaceAll("%NICK%", nickname);
				displayNameFormat = displayNameFormat.replaceAll("%NAME%", playername);
				displayNameFormat = displayNameFormat.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");

				Bukkit.getPlayer(playername).setDisplayName(displayNameFormat);
				Bukkit.getPlayer(playername).setPlayerListName(displayNameFormat);

			}

		}

	}

	public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {

		if (channel.equals("multichat:comm")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				boolean setDisplayName = false;
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

					setDisplayNameLastVal = setDisplayName;
					displayNameFormatLastVal = displayNameFormat;

					updatePlayerMeta(playername, setDisplayName, displayNameFormat);

				}

			} catch (IOException e) {

				System.out.println("[MultiChat] [BRIDGE] Failed to contact bungeecord");
				e.printStackTrace();

			}
		} else if (channel.equals("multichat:action")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String command = in.readUTF();
				getServer().dispatchCommand(getServer().getConsoleSender(), command); 

			} catch (IOException e) {

				System.out.println("[MultiChat] [BRIDGE] Failed to contact bungeecord");
				e.printStackTrace();

			}
		} else if (channel.equals("multichat:paction")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
			DataInputStream in = new DataInputStream(stream);

			try {

				String playerRegex = in.readUTF();
				String command = in.readUTF();

				for (Player p : getServer().getOnlinePlayers()) {

					if (p.getName().matches(playerRegex)) {
						getServer().dispatchCommand(p, command);
					}

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

					updatePlayerMeta(playername, setDisplayNameLastVal, displayNameFormatLastVal);

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
					updatePlayerMeta(sender.getName(), setDisplayNameLastVal, displayNameFormatLastVal);
					sender.sendMessage("You have had your nickname removed!");
					return true;
				}

				if (NameManager.getInstance().containsColorCodes(args[0]) && !(sender.hasPermission("multichatbridge.nick.color") || sender.hasPermission("multichatbridge.nick.colour"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with color codes!");
					return true;
				}

				if (NameManager.getInstance().containsFormatCodes(args[0]) && !(sender.hasPermission("multichatbridge.nick.format"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with format codes!");
					return true;
				}

				if (!simpleNickname.matcher(args[0]).matches() && !(sender.hasPermission("multichatbridge.nick.special"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with special characters!");
					return true;
				}

				if (NameManager.getInstance().stripAllFormattingCodes(args[0]).length() > 20 && !sender.hasPermission("multichatbridge.nick.anylength")) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max 20 characters! (Excluding format codes)");
					return true;

				}

				NameManager.getInstance().setNickname(targetUUID, args[0]);
				updatePlayerMeta(sender.getName(), setDisplayNameLastVal, displayNameFormatLastVal);

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
				updatePlayerMeta(target.getName(), setDisplayNameLastVal, displayNameFormatLastVal);
				sender.sendMessage(ChatColor.GREEN + args[0] + " has had their nickname removed!");
				return true;
			}

			if (NameManager.getInstance().containsColorCodes(args[1]) && !(sender.hasPermission("multichatbridge.nick.color") || sender.hasPermission("multichatbridge.nick.colour"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with color codes!");
				return true;
			}

			if (NameManager.getInstance().containsFormatCodes(args[1]) && !(sender.hasPermission("multichatbridge.nick.format"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with format codes!");
				return true;
			}

			if (!simpleNickname.matcher(args[1]).matches() && !(sender.hasPermission("multichatbridge.nick.special"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with special characters!");
				return true;
			}

			if (NameManager.getInstance().stripAllFormattingCodes(args[1]).length() > 20 && !sender.hasPermission("multichatbridge.nick.anylength")) {

				sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max 20 characters! (Excluding format codes)");
				return true;

			}

			NameManager.getInstance().setNickname(targetUUID, args[1]);
			updatePlayerMeta(target.getName(), setDisplayNameLastVal, displayNameFormatLastVal);

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

			} else if (sender.hasPermission("multichatbridge.realname.partial")) {

				Optional<Set<UUID>> matches = NameManager.getInstance().getPartialNicknameMatches(args[0]);

				if (matches.isPresent()) {

					int limit = 10;

					sender.sendMessage(ChatColor.DARK_AQUA + "No one could be found with the exact nickname: " + args[0]);
					sender.sendMessage(ChatColor.AQUA + "The following were found as partial matches:");

					for (UUID uuid : matches.get()) {

						if (limit > 0 || sender.hasPermission("multichatbridge.realname.nolimit")) {
							sender.sendMessage(ChatColor.GREEN + "Nickname: '" + NameManager.getInstance().getCurrentName(uuid) + "' Belongs to player: '" + NameManager.getInstance().getName(uuid) + "'");
							limit--;
						} else {
							sender.sendMessage(ChatColor.DARK_GREEN + "Only the first 10 results have been shown, please try a more specific query!");
							break;
						}

					}

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

}
