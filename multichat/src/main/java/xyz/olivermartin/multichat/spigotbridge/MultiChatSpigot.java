package xyz.olivermartin.multichat.spigotbridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import xyz.olivermartin.multichat.database.DatabaseManager;
import xyz.olivermartin.multichat.database.DatabaseMode;
import xyz.olivermartin.multichat.spigotbridge.commands.CommandHandler;
import xyz.olivermartin.multichat.spigotbridge.listeners.ChatListenerHighest;
import xyz.olivermartin.multichat.spigotbridge.listeners.ChatListenerLowest;
import xyz.olivermartin.multichat.spigotbridge.listeners.ChatListenerMonitor;
import xyz.olivermartin.multichat.spigotbridge.listeners.LoginListener;
import xyz.olivermartin.multichat.spigotbridge.listeners.MultiChatPluginMessageListener;
import xyz.olivermartin.multichat.spigotbridge.listeners.WorldListener;

/**
 * MultiChatSpigot MAIN CLASS
 * <p>Handles communication with BungeeCord from the SPIGOT side, also controls /nick etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatSpigot extends JavaPlugin implements Listener {

	public static final String pluginName = "MultiChatSpigot";
	public static final String logPrefix = ""; // Legacy

	private static Chat chat = null;
	private static boolean vault;
	private static boolean papi;

	public static String serverName = "SERVER_NAME";

	public static Map<Player, String> playerChannels = new HashMap<Player, String>();
	public static Map<String, PseudoChannel> channelObjects = new HashMap<String, PseudoChannel>();
	public static Map<UUID, Set<UUID>> ignoreMap = new HashMap<UUID, Set<UUID>>();
	public static Map<UUID, Boolean> colourMap = new HashMap<UUID, Boolean>();

	public static Map<String, String> placeholderMap = new HashMap<String, String>();

	public static Optional<Chat> getVaultChat() {
		if (chat == null) return Optional.empty();
		return Optional.of(chat);
	}

	public static boolean hookedVault() {
		return vault;
	}

	public static boolean hookedPAPI() {
		return papi;
	}

	public static File configDir;

	private static final String nameDataFile = "namedata.dat";
	private static File legacyNicknameFile; 

	public static boolean setDisplayNameLastVal = false;
	public static String displayNameFormatLastVal = "%PREFIX%%NICK%%SUFFIX%";
	public static boolean globalChatServer = false;
	public static String globalChatFormat = "&f%DISPLAYNAME%&f: ";

	public static boolean overrideGlobalFormat = false;
	public static String overrideGlobalFormatFormat = "&f%DISPLAYNAME%&f: ";
	public static boolean overrideAllMultiChatFormats = false;
	public static String localChatFormat = "&7&lLOCAL &f> &f%DISPLAYNAME%&f: ";
	public static boolean setLocalFormat = false;
	public static boolean forceMultiChatFormat = false;

	public static boolean showNicknamePrefix = false;
	public static String nicknamePrefix = "~";
	public static List<String> nicknameBlacklist = new ArrayList<String>();
	public static int nicknameMaxLength = 20;
	public static boolean nicknameLengthIncludeFormatting = false;
	public static boolean nicknameSQL = false;

	@SuppressWarnings("unchecked")
	public void onEnable() {

		// Read nickname data

		configDir = getDataFolder().getAbsoluteFile();
		legacyNicknameFile = new File(configDir, "Nicknames.dat");

		if (!getDataFolder().exists()) {
			getLogger().info(logPrefix + "Creating plugin directory!");
			getDataFolder().mkdirs();
			configDir = getDataFolder();
		}

		SpigotConfigManager.getInstance().registerHandler("spigotconfig.yml", configDir);
		Configuration config = SpigotConfigManager.getInstance().getHandler("spigotconfig.yml").getConfig();

		if (config.contains("server_name")) {
			serverName = config.getString("server_name");
		}

		overrideGlobalFormat = config.getBoolean("override_global_format");
		overrideGlobalFormatFormat = config.getString("override_global_format_format");
		overrideAllMultiChatFormats = config.getBoolean("override_all_multichat_formatting");
		setLocalFormat = config.getBoolean("set_local_format");
		localChatFormat = config.getString("local_chat_format");
		forceMultiChatFormat = config.getBoolean("force_multichat_format");

		placeholderMap.clear();
		ConfigurationSection placeholders = config.getConfigurationSection("multichat_placeholders");
		if (placeholders != null) {

			for (String placeholder : placeholders.getKeys(false)) {
				placeholderMap.put("{multichat_" + placeholder + "}", placeholders.getString(placeholder));
			}

		}

		if (config.contains("show_nickname_prefix")) {
			showNicknamePrefix = config.getBoolean("show_nickname_prefix");
			nicknamePrefix = config.getString("nickname_prefix");
			nicknameBlacklist = config.getStringList("nickname_blacklist");
			if (config.contains("nickname_length_limit")) {

				nicknameMaxLength = config.getInt("nickname_length_limit");
				nicknameLengthIncludeFormatting = config.getBoolean("nickname_length_limit_formatting");

			}
			if (config.contains("nickname_sql")) {

				nicknameSQL = config.getBoolean("nickname_sql");

				if (nicknameSQL) {

					if (config.getBoolean("mysql")) {

						DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);

						DatabaseManager.getInstance().setURLMySQL(config.getString("mysql_url"));
						DatabaseManager.getInstance().setUsernameMySQL(config.getString("mysql_user"));
						DatabaseManager.getInstance().setPasswordMySQL(config.getString("mysql_pass"));

						try {

							if (config.contains("mysql_database")) {
								DatabaseManager.getInstance().createDatabase("multichatspigot.db", config.getString("mysql_database"));
							} else {
								DatabaseManager.getInstance().createDatabase("multichatspigot.db", "multichatspigot");
							}

							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().connectToDatabase();
							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().update("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().update("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");
							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().disconnectFromDatabase();

							NameManager.useSQL(nicknameSQL);

						} catch (SQLException e) {
							nicknameSQL = false;
							NameManager.useSQL(false);
							System.err.println("Could not enable database! Using files...");
							e.printStackTrace();
						}

					} else {

						DatabaseManager.getInstance().setMode(DatabaseMode.SQLite);

						DatabaseManager.getInstance().setPathSQLite(configDir);

						try {

							DatabaseManager.getInstance().createDatabase("multichatspigot.db");

							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().connectToDatabase();
							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().update("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().update("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");
							DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().disconnectFromDatabase();

							NameManager.useSQL(nicknameSQL);

						} catch (SQLException e) {
							nicknameSQL = false;
							NameManager.useSQL(false);
							System.err.println("Could not enable database! Using files...");
							e.printStackTrace();
						}

					}

				}

			}
		}

		File f = new File(configDir, nameDataFile);

		if ((f.exists()) && (!f.isDirectory())) {

			getLogger().info(logPrefix + "Attempting startup load for Nicknames");

			File file = new File(configDir, nameDataFile);
			FileInputStream saveFile;
			try {
				saveFile = new FileInputStream(file);
				if (NameManager.getInstance() instanceof FileNameManager) {
					((FileNameManager)NameManager.getInstance()).loadNicknameData(saveFile);
				} else {



					// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					System.err.println("NEED TO IMPLEMENT NICKNAME FILE AND SQL MODE SWITCHING!!!");






				}

			} catch (FileNotFoundException e) {
				getLogger().info(logPrefix + "[ERROR] Could not load nickname data");
				e.printStackTrace();
			}

			getLogger().info(logPrefix + "Load completed!");

		} else if (legacyNicknameFile.exists()) {

			// LEGACY NICKNAME FILE HANDLING --------------------------------------------

			HashMap<UUID, String> result = null;

			try {

				FileInputStream saveFile = new FileInputStream(legacyNicknameFile);
				ObjectInputStream in = new ObjectInputStream(saveFile);
				result = (HashMap<UUID, String>) in.readObject();
				in.close();
				getLogger().info(logPrefix + "Loaded a legacy (pre 1.6) nicknames file. Attempting conversion...");

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
					if (NameManager.getInstance() instanceof FileNameManager) {
						((FileNameManager)NameManager.getInstance()).saveNicknameData(saveFile2);
					} else {



						// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						System.err.println("NEED TO IMPLEMENT NICKNAME FILE AND SQL MODE SWITCHING!!!");






					}
				} catch (FileNotFoundException e) {
					getLogger().info(logPrefix + "[ERROR] Could not save nickname data");
					e.printStackTrace();
				}

				getLogger().info(logPrefix + "The files were created!");

			} catch (IOException|ClassNotFoundException e) {

				getLogger().info(logPrefix + "[ERROR] An error has occured reading the legacy nicknames file. Please delete it.");
				e.printStackTrace();

			}

		} else {

			getLogger().info(logPrefix + "Name data files do not exist to load. Must be first startup!");
			getLogger().info(logPrefix + "Enabling Nicknames! :D");
			getLogger().info(logPrefix + "Attempting to create hash files!");

			File file = new File(configDir, nameDataFile);
			FileOutputStream saveFile;
			try {
				saveFile = new FileOutputStream(file);
				if (NameManager.getInstance() instanceof FileNameManager) {
					((FileNameManager)NameManager.getInstance()).saveNicknameData(saveFile);
				} else {



					// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					System.err.println("NEED TO IMPLEMENT NICKNAME FILE AND SQL MODE SWITCHING!!!");






				}

			} catch (FileNotFoundException e) {
				getLogger().info(logPrefix + "[ERROR] Could not save nickname data");
				e.printStackTrace();
			}

			getLogger().info(logPrefix + "The files were created!");

		}

		// Register plugin communication channels

		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:comm");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:chat");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:prefix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:suffix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:dn");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:world");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:nick");
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:comm", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:chat", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:act", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:pact", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ch", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ignore", MultiChatPluginMessageListener.getInstance());

		// Register listeners

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(NameManager.getInstance(), this);
		getServer().getPluginManager().registerEvents(new ChatListenerHighest(), this);
		getServer().getPluginManager().registerEvents(new ChatListenerLowest(), this);
		getServer().getPluginManager().registerEvents(new ChatListenerMonitor(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new LoginListener(), this);

		// Register commands

		this.getCommand("nick").setExecutor(CommandHandler.getInstance());
		this.getCommand("realname").setExecutor(CommandHandler.getInstance());
		this.getCommand("username").setExecutor(CommandHandler.getInstance());
		this.getCommand("multichatspigot").setExecutor(CommandHandler.getInstance());

		// Manage dependencies

		vault = setupChat();
		papi = setupPAPI();

		if (vault) {
			getLogger().info(logPrefix + "Successfully connected to Vault!");
		}

		if (papi) {
			getLogger().info(logPrefix + "Successfully connected to PlaceholderAPI!");
		}

	}

	private boolean setupChat() {

		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);

		if (rsp == null) {
			getLogger().info(logPrefix + "[ERROR] Vault was found, but will not work properly until you install a compatible permissions plugin!");
			return false;
		}

		chat = rsp.getProvider();
		return chat != null;

	}

	private boolean setupPAPI() {

		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
			return false;
		} else {
			return true;
		}

	}

	public void onDisable() {
		
		try {
			DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().disconnectFromDatabase();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		File file = new File(configDir, nameDataFile);
		FileOutputStream saveFile;
		try {
			saveFile = new FileOutputStream(file);
			if (NameManager.getInstance() instanceof FileNameManager) {
				((FileNameManager)NameManager.getInstance()).saveNicknameData(saveFile);
			} else {



				// TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				System.err.println("NEED TO IMPLEMENT NICKNAME FILE AND SQL MODE SWITCHING!!!");






			}
		} catch (FileNotFoundException e) {
			getLogger().info(logPrefix + "[ERROR] Could not save nickname data");
			e.printStackTrace();
		}
	}

}
