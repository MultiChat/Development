package xyz.olivermartin.multichat.local.spigot;

import java.io.File;
import java.sql.SQLException;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import xyz.olivermartin.multichat.common.MultiChatInfo;
import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.LocalMetaManager;
import xyz.olivermartin.multichat.local.common.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.common.LocalProxyCommunicationManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfigManager;
import xyz.olivermartin.multichat.local.common.storage.LocalDataStore;
import xyz.olivermartin.multichat.local.common.storage.LocalDatabaseSetupManager;
import xyz.olivermartin.multichat.local.common.storage.LocalFileNameManager;
import xyz.olivermartin.multichat.local.common.storage.LocalFileSystemManager;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManager;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManagerMode;
import xyz.olivermartin.multichat.local.common.storage.LocalSQLNameManager;
import xyz.olivermartin.multichat.local.spigot.commands.MultiChatLocalSpigotCommand;
import xyz.olivermartin.multichat.local.spigot.commands.SpigotNickCommand;
import xyz.olivermartin.multichat.local.spigot.commands.SpigotProxyExecuteCommand;
import xyz.olivermartin.multichat.local.spigot.commands.SpigotRealnameCommand;
import xyz.olivermartin.multichat.local.spigot.commands.SpigotUsernameCommand;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotPAPIHook;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotVaultHook;
import xyz.olivermartin.multichat.local.spigot.listeners.LocalSpigotLoginLogoutListener;
import xyz.olivermartin.multichat.local.spigot.listeners.LocalSpigotWorldChangeListener;
import xyz.olivermartin.multichat.local.spigot.listeners.chat.LocalSpigotChatListenerHighest;
import xyz.olivermartin.multichat.local.spigot.listeners.chat.LocalSpigotChatListenerLowest;
import xyz.olivermartin.multichat.local.spigot.listeners.chat.LocalSpigotChatListenerMonitor;
import xyz.olivermartin.multichat.local.spigot.listeners.communication.LocalSpigotActionListener;
import xyz.olivermartin.multichat.local.spigot.listeners.communication.LocalSpigotCastListener;
import xyz.olivermartin.multichat.local.spigot.listeners.communication.LocalSpigotIgnoreListener;
import xyz.olivermartin.multichat.local.spigot.listeners.communication.LocalSpigotPlayerActionListener;
import xyz.olivermartin.multichat.local.spigot.listeners.communication.LocalSpigotPlayerChannelListener;
import xyz.olivermartin.multichat.local.spigot.listeners.communication.LocalSpigotPlayerMetaListener;

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
		api.registerPlatform(platform);

		// Register name
		String pluginName = "MultiChatSpigot";
		api.registerPluginName(pluginName);

		// Register version
		String pluginVersion = MultiChatInfo.LATEST_VERSION;
		api.registerVersion(pluginVersion);

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
		LocalConfigManager configMan = new LocalConfigManager(platform);
		api.registerConfigManager(configMan);

		// Register config files
		configMan.registerLocalConfig("spigotconfig.yml", configDir);

		// Register data store
		LocalDataStore dataStore = new LocalDataStore();
		api.registerDataStore(dataStore);

		// Register name manager...
		LocalNameManager nameManager;

		if (configMan.getLocalConfig().isNicknameSQL()) {

			LocalDatabaseSetupManager ldsm = new LocalDatabaseSetupManager(platform, configMan.getLocalConfig().isMySQL());

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
			fileSystemManager.registerNicknameFile(platform, "namedata.dat", configDir, (LocalFileNameManager)nameManager);
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

		// Register placeholder manager
		LocalPlaceholderManager placeholderManager = new LocalSpigotPlaceholderManager();
		api.registerPlaceholderManager(placeholderManager);

		// Register chat manager
		LocalChatManager chatManager = new LocalSpigotChatManager();
		api.registerChatManager(chatManager);

		// Register Listeners
		getServer().getPluginManager().registerEvents(new LocalSpigotWorldChangeListener(), this);
		getServer().getPluginManager().registerEvents(new LocalSpigotLoginLogoutListener(), this);
		getServer().getPluginManager().registerEvents(new LocalSpigotChatListenerLowest(), this);
		getServer().getPluginManager().registerEvents(new LocalSpigotChatListenerHighest(), this);
		getServer().getPluginManager().registerEvents(new LocalSpigotChatListenerMonitor(), this);

		// Register Commands
		this.getCommand("multichatlocal").setExecutor(new MultiChatLocalSpigotCommand());
		SpigotProxyExecuteCommand pxeCommand = new SpigotProxyExecuteCommand();
		this.getCommand("pxe").setExecutor(pxeCommand);
		this.getCommand("pexecute").setExecutor(pxeCommand);
		this.getCommand("nick").setExecutor(new SpigotNickCommand());
		this.getCommand("realname").setExecutor(new SpigotRealnameCommand());
		this.getCommand("username").setExecutor(new SpigotUsernameCommand());

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

		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:comm", new LocalSpigotPlayerMetaListener());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:chat", new LocalSpigotCastListener());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:act", new LocalSpigotActionListener());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:pact", new LocalSpigotPlayerActionListener());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ch", new LocalSpigotPlayerChannelListener());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ignore", new LocalSpigotIgnoreListener());

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
