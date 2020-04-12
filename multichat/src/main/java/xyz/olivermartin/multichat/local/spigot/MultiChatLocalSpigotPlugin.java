package xyz.olivermartin.multichat.local.spigot;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.local.LocalConfigManager;
import xyz.olivermartin.multichat.local.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.LocalDataStore;
import xyz.olivermartin.multichat.local.LocalDatabaseSetupManager;
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

		// Register console logger
		LocalConsoleLogger consoleLogger = new LocalSpigotConsoleLogger(getLogger());
		api.registerConsoleLogger(consoleLogger);

		// Register platform
		MultiChatLocalPlatform platform = MultiChatLocalPlatform.SPIGOT;
		api.registerPlatform(MultiChatLocalPlatform.SPIGOT);

		// Register name
		String pluginName = "MultiChatSpigot";
		api.registerPluginName(pluginName);

		// Register config directory
		File configDir = getDataFolder().getAbsoluteFile();
		if (!getDataFolder().exists()) {
			consoleLogger.log("Creating plugin directory...");
			getDataFolder().mkdirs();
			configDir = getDataFolder();
		}
		api.registerConfigDirectory(configDir);

		// Create directory for translations
		String translationsDirString = configDir.toString() + File.separator + "translations";
		File translationsDir = new File(translationsDirString);
		if (!translationsDir.exists()) {
			consoleLogger.log("Creating translations directory...");
			translationsDir.mkdirs();
		}

		// Register config manager
		LocalConfigManager configMan = new LocalConfigManager();
		api.registerConfigManager(configMan);

		// Register config files
		configMan.registerLocalConfig(platform, "spigotconfig.yml", configDir);

		// Register data store
		LocalDataStore dataStore = new LocalDataStore();
		api.registerDataStore(dataStore);

		// Register name manager...
		LocalNameManager nameManager;

		if (configMan.getLocalConfig().isNicknameSQL()) {

			LocalDatabaseSetupManager ldsm = new LocalDatabaseSetupManager(MultiChatLocalPlatform.SPIGOT, configMan.getLocalConfig().isMySQL());

			if (ldsm.isConnected()) {
				nameManager = new LocalSQLNameManager("multichatspigot.db");
			} else {
				consoleLogger.log("Could not connect to database! Using file based storage instead...");
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

		// Copy translations files...
		fileSystemManager.createResource("spigotconfig_fr.yml", translationsDir);

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
			MultiChatLocal.getInstance().getConsoleLogger().log("[ERROR] Vault was found, but will not work properly until you install a compatible permissions plugin!");
			return;
		}

		LocalSpigotVaultHook.getInstance().hook(rsp.getProvider());
		MultiChatLocal.getInstance().getConsoleLogger().log("MultiChatLocal hooked with Vault!");

	}

	private void setupPAPI() {

		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
			return;
		} else {
			LocalSpigotPAPIHook.getInstance().hook();
			MultiChatLocal.getInstance().getConsoleLogger().log("MultiChatLocal hooked with PlaceholderAPI!");
		}

	}

	@Override
	public void onDisable() {

		if (MultiChatLocal.getInstance().getNameManager().getMode() == LocalNameManagerMode.SQL) {

			try {
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().disconnectFromDatabase();
			} catch (SQLException e) {
				MultiChatLocal.getInstance().getConsoleLogger().log("Error when disconnecting from database!");
			}

		} else {

			MultiChatLocal.getInstance().getFileSystemManager().getNicknameFile().save();

		}

	}

}
