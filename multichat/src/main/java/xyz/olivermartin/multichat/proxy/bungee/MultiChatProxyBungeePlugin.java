package xyz.olivermartin.multichat.proxy.bungee;

import java.io.File;

import net.md_5.bungee.api.plugin.Plugin;
import xyz.olivermartin.multichat.common.MultiChatInfo;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.ProxyMessageManager;
import xyz.olivermartin.multichat.proxy.common.ProxyMessagingServicePlayerMetaStore;
import xyz.olivermartin.multichat.proxy.common.ProxyModuleManager;
import xyz.olivermartin.multichat.proxy.common.ProxyPlayerManager;
import xyz.olivermartin.multichat.proxy.common.ProxyPlayerMetaStore;
import xyz.olivermartin.multichat.proxy.common.channels.ProxyChannelManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigManager;
import xyz.olivermartin.multichat.proxy.common.modules.staffchat.StaffChatModule;
import xyz.olivermartin.multichat.proxy.common.store.ProxyFileDataStoreManager;

public class MultiChatProxyBungeePlugin extends Plugin {

	@Override
	public void onEnable() {

		// BSTATS METRICS
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);

		// GET API
		MultiChatProxy api = MultiChatProxy.getInstance();

		// REGISTER PLATFORM
		MultiChatProxyPlatform platform = MultiChatProxyPlatform.BUNGEE;
		api.registerPlatform(platform);

		// REGISTER NAME
		String pluginName = "MultiChat";
		api.registerPluginName(pluginName);

		// REGISTER VERSION
		String pluginVersion = MultiChatInfo.LATEST_VERSION;
		api.registerPluginVersion(pluginVersion);

		// REGISTER CONFIG DIRECTORY
		File configDir = getDataFolder();
		if (!getDataFolder().exists()) {
			getLogger().info("Creating plugin directory for MultiChat!");
			getDataFolder().mkdirs();
		}
		api.registerConfigDirectory(configDir);

		// REGISTER TRANSLATIONS DIRECTORY
		File translationsDir = new File(configDir.toString() + File.separator + "translations");
		if (!translationsDir.exists()) {
			getLogger().info("Creating translations directory for MultiChat!");
			translationsDir.mkdirs();
		}

		// REGISTER DATA STORE DIRECTORY
		File dataStoreDir = new File(configDir.toString() + File.separator + "data");
		if (!dataStoreDir.exists()) {
			getLogger().info("Creating data store directory for MultiChat!");
			dataStoreDir.mkdirs();
		}

		// REGISTER CONFIG MANAGER
		ProxyConfigManager configManager = new ProxyConfigManager(platform);
		MultiChatProxy.getInstance().registerConfigManager(configManager);

		// REGISTER CONFIG FILES
		configManager.registerProxyMainConfig("config.yml", configDir);
		configManager.registerProxyJoinMessagesConfig("joinmessages.yml", configDir);
		configManager.registerProxyChatControlConfig("chatcontrol.yml", configDir);
		configManager.registerProxyMessagesConfig("messages.yml", configDir);

		// REGISTER MESSAGE MANAGER
		ProxyMessageManager messageManager = new ProxyMessageManager(configManager.getProxyMessagesConfig());
		api.registerMessageManager(messageManager);

		// REGISTER PLAYER META STORE
		ProxyPlayerMetaStore metaStore = new ProxyMessagingServicePlayerMetaStore();
		api.registerPlayerMetaStore(metaStore);

		// REGISTER PLAYER MANAGER
		ProxyPlayerManager playerManager = new ProxyPlayerManager();
		api.registerPlayerManager(playerManager);

		// REGISTER MODULE MANAGER
		ProxyModuleManager moduleManager = new ProxyModuleManager();
		api.registerModuleManager(moduleManager);

		// TODO IF ENABLED...
		StaffChatModule staffChat = new StaffChatModule();
		moduleManager.registerStaffChatModule(staffChat);

		// TODO Check config version

		// TODO Copy the translations files using a file system manager

		// TODO configure data store mode...
		ProxyFileDataStoreManager fileDataStoreManager = new ProxyFileDataStoreManager(MultiChatProxyPlatform.BUNGEE);
		fileDataStoreManager.registerChannelsFileDataStore(dataStoreDir, "channels_data_store.dat");
		fileDataStoreManager.registerNameFileDataStore(dataStoreDir, "name_data_store.dat");
		api.registerDataStoreManager(fileDataStoreManager);

		ProxyChannelManager channelManager = new ProxyChannelManager();
		api.registerChannelManager(channelManager);

		// TODO register listeners

		// TODO register communication channels

		// TODO register commands

		// TODO run "start up routines"???!!!

		// TODO "setup chat control stuff?!"

		// TODO "Set default channel"

		// TODO "Set up global chat"

		// TODO "Add all appropriate servers to hard-coded global chat stream..."

		// TODO initiate backup routine

		// TODO fetch display names of all players...

	}

}
