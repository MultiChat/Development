package xyz.olivermartin.multichat.local.spigot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.common.database.DatabaseMode;
import xyz.olivermartin.multichat.local.LocalConfigManager;
import xyz.olivermartin.multichat.local.LocalDataStore;
import xyz.olivermartin.multichat.local.LocalDatabaseCredentials;
import xyz.olivermartin.multichat.local.LocalFileNameManager;
import xyz.olivermartin.multichat.local.LocalFileSystemManager;
import xyz.olivermartin.multichat.local.LocalMetaManager;
import xyz.olivermartin.multichat.local.LocalNameManager;
import xyz.olivermartin.multichat.local.LocalNameManagerMode;
import xyz.olivermartin.multichat.local.LocalSQLNameManager;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.communication.LocalProxyCommunicationManager;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotPAPIHook;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotVaultHook;
import xyz.olivermartin.multichat.spigotbridge.listeners.MultiChatPluginMessageListener;

public class MultiChatLocalSpigotPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		// GET API
		MultiChatLocal api = MultiChatLocal.getInstance();

		// Register platform
		MultiChatLocalPlatform platform = MultiChatLocalPlatform.SPIGOT;
		api.registerPlatform(MultiChatLocalPlatform.SPIGOT);

		// Register name
		String pluginName = "MultiChatSpigot";
		api.registerPluginName(pluginName);

		// Register config directory
		File configDir = getDataFolder().getAbsoluteFile();
		if (!getDataFolder().exists()) {
			//TODO Need to use logger properly! getLogger().info(logPrefix + "Creating plugin directory!");
			getDataFolder().mkdirs();
			configDir = getDataFolder();
		}
		api.registerConfigDirectory(configDir);

		// Create directory for translations
		String translationsDirString = configDir.toString() + File.separator + "translations";
		File translationsDir = new File(translationsDirString);
		if (!translationsDir.exists()) {
			//TODO logger getLogger().info(logPrefix + "Creating translations directory!");
			translationsDir.mkdirs();
		}

		// Register config manager
		LocalConfigManager configMan = new LocalConfigManager();
		api.registerConfigManager(configMan);

		// Register config files
		configMan.registerLocalConfig(platform, "spigotconfig.yml", configDir);
		// TODO Need a new way to copy the translations config... configMan.registerLocalConfig(platform, "spigotconfig_fr.yml", translationsDir);

		// Register data store
		LocalDataStore dataStore = new LocalDataStore();
		api.registerDataStore(dataStore);

		// Register name manager...
		LocalNameManager nameManager;

		// TODO Move setup database to somewhere else? --> LocalDatabaseManager can definitely do this.
		if (configMan.getLocalConfig().isNicknameSQL()) {

			try {

				if (configMan.getLocalConfig().isMySQL()) {

					// MYSQL SETTINGS

					DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);

					DatabaseManager.getInstance().setURLMySQL(LocalDatabaseCredentials.getInstance().getURL());
					DatabaseManager.getInstance().setUsernameMySQL(LocalDatabaseCredentials.getInstance().getUser());
					DatabaseManager.getInstance().setPasswordMySQL(LocalDatabaseCredentials.getInstance().getPassword());

					DatabaseManager.getInstance().createDatabase("multichatspigot.db", LocalDatabaseCredentials.getInstance().getDatabase());

				} else {

					// SQLITE SETTINGS

					DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);
					DatabaseManager.getInstance().setPathSQLite(configDir);

					DatabaseManager.getInstance().createDatabase("multichatspigot.db");

				}

				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().connectToDatabase();
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().safeUpdate("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().safeUpdate("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");

				nameManager = new LocalSQLNameManager("multichatspigot.db");

			} catch (SQLException e) {

				// TODO Show error?!
				nameManager = new LocalSpigotFileNameManager();

			}

		} else {

			nameManager = new LocalSpigotFileNameManager();

		}

		api.registerNameManager(nameManager);

		LocalFileSystemManager fileSystemManager = new LocalFileSystemManager();
		api.registerFileSystemManager(fileSystemManager);

		// If we are using file based storage for name data, then register and load the nickname file into name manager
		if (nameManager.getMode() == LocalNameManagerMode.FILE) {
			fileSystemManager.registerNicknameFile(MultiChatLocalPlatform.SPIGOT, "namedata.dat", configDir);
		}

		// Register meta manager
		LocalMetaManager metaManager = new LocalSpigotMetaManager();
		api.registerMetaManager(metaManager);

		// Register plugin communication channels
		registerCommunicationChannels();

		// Register communication manager
		LocalProxyCommunicationManager proxyCommunicationManager = new SpigotBungeeCommunicationManager();
		api.registerProxyCommunicationManager(proxyCommunicationManager);

		// Register Listeners

		// Register Commands

		// Manage dependencies
		setupVaultChat();
		setupPAPI();

	}

	private void registerCommunicationChannels() {

		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:comm");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:chat");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:prefix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:suffix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:dn");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:world");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:nick");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:pxe");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:ppxe");

		// TODO NEED TO MAKE LOCAL VERSIONS OF THESE LISTENERS!!!
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:comm", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:chat", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:act", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:pact", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ch", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ignore", MultiChatPluginMessageListener.getInstance());

	}

	private void setupVaultChat() {

		if (getServer().getPluginManager().getPlugin("Vault") == null) return;

		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);

		if (rsp == null) {
			// TODO Logger
			getLogger().info("[ERROR] Vault was found, but will not work properly until you install a compatible permissions plugin!");
			return;
		}

		LocalSpigotVaultHook.getInstance().hook(rsp.getProvider());

	}

	private void setupPAPI() {

		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
			return;
		} else {
			LocalSpigotPAPIHook.getInstance().hook();
		}

	}

	@Override
	public void onDisable() {

		// TODO make more generic!

		if (MultiChatLocal.getInstance().getNameManager().getMode() == LocalNameManagerMode.SQL) {

			try {
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().disconnectFromDatabase();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} else {

			/* LEGACY! */

			LocalFileNameManager fileNameManager = (LocalFileNameManager) MultiChatLocal.getInstance().getNameManager();

			String nameDataFile = "namedata.dat";
			File file = new File(MultiChatLocal.getInstance().getConfigDirectory(), nameDataFile);
			FileOutputStream saveFile;
			try {
				saveFile = new FileOutputStream(file);

				ObjectOutputStream out = new ObjectOutputStream(saveFile);

				out.writeObject(fileNameManager.getMapUUIDNick());
				out.writeObject(fileNameManager.getMapUUIDName());
				out.writeObject(fileNameManager.getMapNickUUID());
				out.writeObject(fileNameManager.getMapNameUUID());
				out.writeObject(fileNameManager.getMapNickFormatted());
				out.writeObject(fileNameManager.getMapNameFormatted());

				out.close();


			} catch (IOException e) {
				getLogger().info("[ERROR] Could not save nickname data");
				e.printStackTrace();
			}

		}

	}

}
