package xyz.olivermartin.multichat.bungee;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.ProxyBackupManager;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.local.LocalChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.GlobalStaticProxyChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.ProxyChannelInfo;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.StaticProxyChannel;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;
import xyz.olivermartin.multichat.proxy.common.contexts.ContextManager;
import xyz.olivermartin.multichat.proxy.common.contexts.GlobalContext;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyLoginListener;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyLogoutListener;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyServerSwitchListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyPlayerActionListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyPlayerChatListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyPlayerMetaListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyServerActionListener;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyFileStoreManager;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyAdminChatFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyAnnouncementsFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyBulletinsFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyCastsFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyGlobalChatFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyGroupChatFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyGroupSpyFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyIgnoreFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyMuteFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxySocialSpyFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyStaffChatFileStore;
import xyz.olivermartin.multichat.proxy.common.storage.files.ProxyUUIDNameFileStore;


/**
 * The MAIN MultiChat Class
 * <p>This class is the main plugin. All plugin enable and disable control happens here.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChat extends Plugin implements Listener {

	public static final String LATEST_VERSION = "1.10";

	public static final String[] ALLOWED_VERSIONS = new String[] {

			LATEST_VERSION,
			"1.9.5",
			"1.9.4",
			"1.9.3",
			"1.9.2",
			"1.9.1",
			"1.9",
			"1.8.2",
			"1.8.1",
			"1.8",
			"1.7.5",
			"1.7.4",
			"1.7.3",
			"1.7.2",
			"1.7.1",
			"1.7",
			"1.6.2",
			"1.6.1",
			"1.6",
			"1.5.2",
			"1.5.1",
			"1.5",
			"1.4.2",
			"1.4.1",
			"1.4",
			"1.3.4",
			"1.3.3",
			"1.3.2",
			"1.3.1",
			"1.3"

	};

	public static String configversion;

	// Config values

	public static boolean logPMs = true;
	public static boolean logStaffChat = true;
	public static boolean logGroupChat = true;

	public static boolean premiumVanish = false;
	public static boolean hideVanishedStaffInMsg = true;
	public static boolean hideVanishedStaffInStaffList = true;
	public static boolean hideVanishedStaffInJoin = true;

	public static List<String> legacyServers = new ArrayList<String>();

	public void fetchDisplayNames() {

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (player.getServer() != null) {
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
						}
					}

				}

			}

		}, 1L, 5L, TimeUnit.MINUTES);

	}

	@EventHandler
	public void onLogin(PostLoginEvent event) {

		fetchDisplayNameOnce(event.getPlayer().getName());

	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {

		fetchDisplayNameOnce(event.getPlayer().getName());

	}

	public void fetchDisplayNameOnce(final String playername) {

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						if (player.getServer() != null) {
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
						}

					}
				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 0L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						if (player.getServer() != null) {
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
						}

					}
				}

				catch (NullPointerException ex) { /* EMPTY */ }
			}

		}, 1L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						if (player.getServer() != null) {
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
						}

					}

				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 2L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						if (player.getServer() != null) {
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
						}

					}

				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 4L, TimeUnit.SECONDS);

	}

	public void onEnable() {

		MultiChatProxy.getInstance().registerPlugin(this);

		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);

		MultiChatProxyPlatform platform = MultiChatProxyPlatform.BUNGEE;
		MultiChatProxy.getInstance().registerPlatform(platform);

		ProxyDataStore dataStore = new ProxyDataStore();
		MultiChatProxy.getInstance().registerDataStore(dataStore);

		File configDirectory = getDataFolder();
		if (!getDataFolder().exists()) {
			System.out.println("[MultiChat] Creating plugin directory!");
			getDataFolder().mkdirs();
		}
		MultiChatProxy.getInstance().registerConfigDirectory(configDirectory);

		String translationsDir = configDirectory.toString() + File.separator + "translations";
		if (!new File(translationsDir).exists()) {
			System.out.println("[MultiChat] Creating translations directory!");
			new File(translationsDir).mkdirs();
		}

		ConfigManager.getInstance().registerHandler("config.yml", configDirectory);
		ConfigManager.getInstance().registerHandler("joinmessages.yml", configDirectory);
		ConfigManager.getInstance().registerHandler("messages.yml", configDirectory);
		ConfigManager.getInstance().registerHandler("chatcontrol.yml", configDirectory);
		ConfigManager.getInstance().registerHandler("aliases.yml", configDirectory);

		ConfigManager.getInstance().registerHandler("messages_fr.yml", new File(translationsDir));
		ConfigManager.getInstance().registerHandler("joinmessages_fr.yml", new File(translationsDir));
		ConfigManager.getInstance().registerHandler("config_fr.yml", new File(translationsDir));
		ConfigManager.getInstance().registerHandler("chatcontrol_fr.yml", new File(translationsDir));
		ConfigManager.getInstance().registerHandler("aliases_fr.yml", new File(translationsDir));

		Configuration configYML = ConfigManager.getInstance().getHandler("config.yml").getConfig();
		Configuration chatcontrolYML = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		configversion = configYML.getString("version");

		if (Arrays.asList(ALLOWED_VERSIONS).contains(configversion)) {

			if (!configversion.equals(LATEST_VERSION))  {
				getLogger().warning("YOUR CONFIG FILES ARE NOT THE LATEST VERSION");
				getLogger().warning("SOME FEATURES OF MULTICHAT ARE ONLY PRESENT IN THE LATEST VERSION OF THE CONFIG");
			}

			// Register listeners
			getProxy().getPluginManager().registerListener(this, new Events());
			getProxy().getPluginManager().registerListener(this, this);

			// New listeners (1.10+)
			getProxy().getPluginManager().registerListener(this, new ProxyLoginListener());
			getProxy().getPluginManager().registerListener(this, new ProxyLogoutListener());
			getProxy().getPluginManager().registerListener(this, new ProxyServerSwitchListener());

			// Communication Channels
			getProxy().registerChannel(CommChannels.PLAYER_META); // pmeta
			getProxy().registerChannel(CommChannels.PLAYER_CHAT); // pchat
			getProxy().registerChannel(CommChannels.SERVER_CHAT); // schat
			getProxy().registerChannel(CommChannels.PLAYER_ACTION); // pact
			getProxy().registerChannel(CommChannels.SERVER_ACTION); // sact
			getProxy().registerChannel(CommChannels.PLAYER_DATA); // pdata
			getProxy().registerChannel(CommChannels.SERVER_DATA); // sdata
			getProxy().getPluginManager().registerListener(this, new ProxyPlayerMetaListener()); // list - pmeta
			getProxy().getPluginManager().registerListener(this, new ProxyPlayerChatListener()); // list - pchat
			getProxy().getPluginManager().registerListener(this, new ProxyPlayerActionListener()); // list - pact
			getProxy().getPluginManager().registerListener(this, new ProxyServerActionListener()); // list - sact

			// Register commands
			registerCommands(configYML, chatcontrolYML);

			System.out.println("[MultiChat] Config Version: " + configversion);

			// Run start-up routines
			ProxyFileStoreManager fileStoreManager = new ProxyFileStoreManager();

			fileStoreManager.registerFileStore("announcements.dat",
					new ProxyAnnouncementsFileStore("Announcements.dat", configDirectory));

			fileStoreManager.registerFileStore("bulletins.dat",
					new ProxyBulletinsFileStore("Bulletins.dat", configDirectory));

			fileStoreManager.registerFileStore("staffchatinfo.dat",
					new ProxyStaffChatFileStore("StaffChatInfo.dat", configDirectory));

			fileStoreManager.registerFileStore("adminchatinfo.dat",
					new ProxyAdminChatFileStore("AdminChatInfo.dat", configDirectory));

			fileStoreManager.registerFileStore("groupchatinfo.dat",
					new ProxyGroupChatFileStore("GroupChatInfo.dat", configDirectory));

			fileStoreManager.registerFileStore("groupspyinfo.dat",
					new ProxyGroupSpyFileStore("GroupSpyInfo.dat", configDirectory));

			fileStoreManager.registerFileStore("casts.dat",
					new ProxyCastsFileStore("Casts.dat", configDirectory));

			fileStoreManager.registerFileStore("socialspyinfo.dat",
					new ProxySocialSpyFileStore("SocialSpyInfo.dat", configDirectory));

			fileStoreManager.registerFileStore("globalchatinfo.dat",
					new ProxyGlobalChatFileStore("GlobalChatInfo.dat", configDirectory));

			fileStoreManager.registerFileStore("mute.dat",
					new ProxyMuteFileStore("Mute.dat", configDirectory));

			fileStoreManager.registerFileStore("ignore.dat",
					new ProxyIgnoreFileStore("Ignore.dat", configDirectory));

			fileStoreManager.registerFileStore("multichatuuidname.dat",
					new ProxyUUIDNameFileStore("MultiChatUUIDName.dat", configDirectory));

			MultiChatProxy.getInstance().registerFileStoreManager(fileStoreManager);

			// Set up chat control stuff
			if (chatcontrolYML.contains("link_control")) {
				ChatControl.controlLinks = chatcontrolYML.getBoolean("link_control");
				ChatControl.linkMessage = chatcontrolYML.getString("link_removal_message");
				if (chatcontrolYML.contains("link_regex")) {
					ChatControl.linkRegex = chatcontrolYML.getString("link_regex");
				}
			}

			if (configYML.contains("privacy_settings")) {
				logPMs = configYML.getSection("privacy_settings").getBoolean("log_pms");
				logStaffChat = configYML.getSection("privacy_settings").getBoolean("log_staffchat");
				logGroupChat = configYML.getSection("privacy_settings").getBoolean("log_groupchat");
			}

			// Legacy servers for RGB approximation
			if (configYML.contains("legacy_servers")) {
				legacyServers = configYML.getStringList("legacy_servers");
			}

			// Set default channel
			String defaultChannel = configYML.getString("default_channel");
			boolean forceChannelOnJoin = configYML.getBoolean("force_channel_on_join");

			// Set up global chat
			//GlobalChannel channel = LegacyChannel.getGlobalChannel();
			//channel.setFormat(configYML.getString("globalformat"));

			List<String> noGlobalServers = new ArrayList<String>();

			// Add all appropriate servers to this hardcoded global chat stream
			for (String server : configYML.getStringList("no_global")) {
				//channel.addServer(server);
				noGlobalServers.add(server);
			}

			///

			// New context manager and channels
			GlobalContext globalContext = new GlobalContext(defaultChannel, forceChannelOnJoin, true, noGlobalServers);
			ContextManager contextManager = new ContextManager(globalContext);
			MultiChatProxy.getInstance().registerContextManager(contextManager);

			ChannelManager channelManager = new ChannelManager();
			channelManager.setGlobalChannel(new GlobalStaticProxyChannel("Global Channel", configYML.getString("globalformat"), ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("global"), channelManager));
			channelManager.setLocalChannel(new LocalChannel("Local Channel", configYML.getString("globalformat"), ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("local"), channelManager));
			MultiChatProxy.getInstance().registerChannelManager(channelManager);

			// TODO This is just a test channel
			channelManager.registerProxyChannel(new StaticProxyChannel("test", new ProxyChannelInfo("A test channel", "&8[&7TEST&8] &f%DISPLAYNAME%&f: ", false, globalContext, new ArrayList<String>(), "multichat.chat.channel.test", "multichat.chat.channel.test.view"), channelManager));

			///

			// Initiate backup routine
			ProxyBackupManager backupManager = new ProxyBackupManager();
			MultiChatProxy.getInstance().registerBackupManager(backupManager);
			backupManager.registerBackupTask(new Runnable() {

				public void run() {
					MultiChatProxy.getInstance().getFileStoreManager().save();
				}

			});
			backupManager.startBackup(1L, 60L, TimeUnit.MINUTES);

			// Fetch display names of all players
			fetchDisplayNames();

			// Manage premiumVanish dependency
			if (ProxyServer.getInstance().getPluginManager().getPlugin("PremiumVanish") != null) {
				premiumVanish = true;
				System.out.println("[MultiChat] Hooked with PremiumVanish!");

				if (configYML.contains("premium_vanish")) {
					hideVanishedStaffInMsg = configYML.getBoolean("premium_vanish.prevent_message");
					hideVanishedStaffInStaffList = configYML.getBoolean("premium_vanish.prevent_staff_list");
					hideVanishedStaffInJoin = configYML.getBoolean("premium_vanish.silence_join");
				}

			}

		} else {
			getLogger().info("Config incorrect version! Please repair or delete it!");
		}
	}

	public void onDisable() {

		getLogger().info("Thankyou for using MultiChat. Disabling...");

		MultiChatProxy.getInstance().getFileStoreManager().save();

	}

	public void registerCommands(Configuration configYML, Configuration chatcontrolYML) {

		// Register main commands
		getProxy().getPluginManager().registerCommand(this, CommandManager.getAcc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getAc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMcc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getGc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getGroup());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getGrouplist());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMultichat());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMultichatBypass());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMultiChatExecute());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getDisplay());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getFreezechat());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getHelpme());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getClearchat());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getAnnouncement());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getBulletin());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getCast());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getUsecast());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getIgnore());

		// Register PM commands
		if (configYML.getBoolean("pm")) {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getMsg());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getReply());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getSocialspy());
		}

		// Register global chat commands
		if (configYML.getBoolean("global")) {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getLocal());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getGlobal());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getChannel());
		}

		// Register staff list command /staff
		if (configYML.contains("staff_list")) {
			if (configYML.getBoolean("staff_list")) {
				getProxy().getPluginManager().registerCommand(this, CommandManager.getStafflist());
			}
		} else {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getStafflist());
		}

		// Register mute command
		if (chatcontrolYML.getBoolean("mute")) {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getMute());
		}

	}

	public void unregisterCommands(Configuration configYML, Configuration chatcontrolYML) {

		// Unregister main commands
		getProxy().getPluginManager().unregisterCommand(CommandManager.getAcc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getAc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMcc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getGc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getGroup());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getGrouplist());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMultichat());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMultichatBypass());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMultiChatExecute());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getDisplay());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getFreezechat());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getHelpme());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getClearchat());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getAnnouncement());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getBulletin());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getCast());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getUsecast());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getIgnore());

		// Unregister PM commands
		if (configYML.getBoolean("pm")) {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getMsg());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getReply());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getSocialspy());
		}

		// Unregister global chat commands
		if (configYML.getBoolean("global")) {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getLocal());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getGlobal());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getChannel());
		}

		// Unregister staff list command /staff
		if (configYML.contains("staff_list")) {
			if (configYML.getBoolean("staff_list")) {
				getProxy().getPluginManager().unregisterCommand(CommandManager.getStafflist());
			}
		} else {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getStafflist());
		}

		// UnRegister mute command
		if (chatcontrolYML.getBoolean("mute")) {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getMute());
		}

	}

}
