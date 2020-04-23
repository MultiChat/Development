package xyz.olivermartin.multichat.proxy.common;

import java.io.File;

import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigManager;

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
	private String pluginName;
	private File configDirectory;
	private ProxyPlayerMetaStore playerMetaStore;
	private ProxyPlayerManager playerManager;
	private ProxyConfigManager configManager;
	private ProxyMessageManager messageManager;
	private ProxyConsoleLogger consoleLogger;

	/* END ATTRIBUTES */

	private MultiChatProxy() { /* EMPTY */ }

	/**
	 * Register the platform MultiChatProxy is running on
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param platform The platform MultiChatProxy is running on
	 */
	public void registerPlatform(MultiChatProxyPlatform platform) {
		this.platform = platform;
	}

	/**
	 * Get the platform currently being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The platform being used
	 */
	public MultiChatProxyPlatform getPlatform() {
		if (this.platform == null) throw new IllegalStateException("MultiChatProxy platform has not been registered");
		return this.platform;
	}

	/**
	 * Register the name of the MultiChatProxy plugin (i.e. usually will be MultiChat)
	 * 
	 * <p>It is important that this matches the name in bungee.yml or equivalent!</p>
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param pluginName The name of the MultiChatProxy plugin
	 */
	public void registerPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	/**
	 * Get the name of the MultiChatProxy plugin (i.e. Usually just MultiChat ...)
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * s
	 * @return The name of the MultiChatProxy plugin
	 */
	public String getPluginName() {
		if (this.pluginName == null) throw new IllegalStateException("MultiChatProxy plugin name has not been registered");
		return this.pluginName;
	}

	/**
	 * Register the config directory being used by MultiChatProxy
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param configDirectory The config directory being used by MultiChatProxy
	 */
	public void registerConfigDirectory(File configDirectory) {
		this.configDirectory = configDirectory;
	}

	/**
	 * Get the config directory currently being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The config directory being used
	 */
	public File getConfigDirectory() {
		if (this.configDirectory == null) throw new IllegalStateException("MultiChatProxy config directory has not been registered");
		return this.configDirectory;
	}

	/**
	 * Register the player meta store to be used by MultiChatProxy
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param playerMetaStore The player meta store to register to the API
	 */
	public void registerPlayerMetaStore(ProxyPlayerMetaStore playerMetaStore) {
		this.playerMetaStore = playerMetaStore;
	}

	/**
	 * Get the player meta store being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The proxy player meta store
	 */
	public ProxyPlayerMetaStore getPlayerMetaStore() {
		if (this.playerMetaStore == null) throw new IllegalStateException("No MultiChat proxy player meta store has been registered");
		return this.playerMetaStore;
	}

	/**
	 * Register the player manager to be used by MultiChatProxy
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param playerManager The player manager to register to the API
	 */
	public void registerPlayerManager(ProxyPlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	/**
	 * Get the player manager being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The proxy player manager
	 */
	public ProxyPlayerManager getPlayerManager() {
		if (this.playerManager == null) throw new IllegalStateException("No MultiChat proxy player manager has been registered");
		return this.playerManager;
	}

	/**
	 * Register the config manager to be used by MultiChatProxy
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param configManager The config manager to register to the API
	 */
	public void registerConfigManager(ProxyConfigManager configManager) {
		this.configManager = configManager;
	}

	/**
	 * Get the config manager being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The proxy config manager
	 */
	public ProxyConfigManager getConfigManager() {
		if (this.configManager == null) throw new IllegalStateException("No MultiChat proxy config manager has been registered");
		return this.configManager;
	}

	/**
	 * Register the message manager to be used by MultiChatProxy
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param messageManager The message manager to register to the API
	 */
	public void registerMessageManager(ProxyMessageManager messageManager) {
		this.messageManager = messageManager;
	}

	/**
	 * Get the message manager being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The proxy message manager
	 */
	public ProxyMessageManager getMessageManager() {
		if (this.messageManager == null) throw new IllegalStateException("No MultiChat proxy message manager has been registered");
		return this.messageManager;
	}

	/**
	 * Register the console logger to be used by MultiChatProxy
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param consoleLogger The console logger to register to the API
	 */
	public void registerConsoleLogger(ProxyConsoleLogger consoleLogger) {
		this.consoleLogger = consoleLogger;
	}

	/**
	 * Get the console logger being used by MultiChatProxy
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The proxy console logger
	 */
	public ProxyConsoleLogger getConsoleLogger() {
		if (this.consoleLogger == null) throw new IllegalStateException("No MultiChat proxy console logger has been registered");
		return this.consoleLogger;
	}

}
