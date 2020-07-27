package xyz.olivermartin.multichat.proxy.common;

import java.io.File;

import net.md_5.bungee.api.plugin.Plugin;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.contexts.ContextManager;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyFileStoreManager;

/**
 * This is MultiChat's API running on the network proxy server
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatProxy {

	private static MultiChatProxy instance;

	static {
		instance = new MultiChatProxy();
	}

	public static MultiChatProxy getInstance() {
		return instance;
	}

	/* END STATIC */

	private MultiChatProxyPlatform platform;
	private Plugin plugin;
	private File configDirectory;
	private ProxyDataStore dataStore;
	private ProxyFileStoreManager fileStoreManager;
	private ProxyBackupManager backupManager;
	private ContextManager contextManager;
	private ChannelManager channelManager;
	private ProxyChatManager chatManager;

	/* END ATTRIBUTES */

	private MultiChatProxy() { /* EMPTY */ }

	public Plugin getPlugin() {
		return this.plugin;
	}

	public void registerPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public MultiChatProxyPlatform getPlatform() {
		return this.platform;
	}

	public void registerPlatform(MultiChatProxyPlatform platform) {
		this.platform = platform;
	}

	public File getConfigDirectory() {
		return this.configDirectory;
	}

	public void registerConfigDirectory(File configDirectory) {
		this.configDirectory = configDirectory;
	}

	public ProxyDataStore getDataStore() {
		return this.dataStore;
	}

	public void registerDataStore(ProxyDataStore dataStore) {
		this.dataStore = dataStore;
	}

	public ProxyFileStoreManager getFileStoreManager() {
		return this.fileStoreManager;
	}

	public void registerFileStoreManager(ProxyFileStoreManager fileStoreManager) {
		this.fileStoreManager = fileStoreManager;
	}

	public ProxyBackupManager getBackupManager() {
		return this.backupManager;
	}

	public void registerBackupManager(ProxyBackupManager backupManager) {
		this.backupManager = backupManager;
	}

	public ContextManager getContextManager() {
		return this.contextManager;
	}

	public void registerContextManager(ContextManager contextManager) {
		this.contextManager = contextManager;
	}

	public ChannelManager getChannelManager() {
		return this.channelManager;
	}

	public void registerChannelManager(ChannelManager channelManager) {
		this.channelManager = channelManager;
	}

	public ProxyChatManager getChatManager() {
		return this.chatManager;
	}

	public void registerChatManager(ProxyChatManager chatManager) {
		this.chatManager = chatManager;
	}

}
