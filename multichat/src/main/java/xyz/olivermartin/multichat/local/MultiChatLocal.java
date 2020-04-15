package xyz.olivermartin.multichat.local;

import java.io.File;

import xyz.olivermartin.multichat.local.communication.LocalProxyCommunicationManager;
import xyz.olivermartin.multichat.local.config.LocalConfigManager;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;
import xyz.olivermartin.multichat.local.storage.LocalFileSystemManager;
import xyz.olivermartin.multichat.local.storage.LocalNameManager;

/**
 * This is MultiChat's API local to each server (not the proxy)
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatLocal {

	private static MultiChatLocal instance;

	static {
		instance = new MultiChatLocal();
	}

	public static MultiChatLocal getInstance() {
		return instance;
	}

	/* END STATIC */

	private MultiChatLocalPlatform platform;
	private String pluginName;
	private File configDirectory;
	private LocalNameManager nameManager;
	private LocalConfigManager configManager;
	private LocalDataStore dataStore;
	private LocalMetaManager metaManager;
	private LocalProxyCommunicationManager proxyCommunicationManager;
	private LocalFileSystemManager fileSystemManager;
	private LocalConsoleLogger consoleLogger;
	private LocalPlaceholderManager placeholderManager;
	private LocalChatManager chatManager;

	/* END ATTRIBUTES */

	private MultiChatLocal() { /* EMPTY */ }

	/**
	 * Register the platform MultiChatLocal is running on
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param platform The platform MultiChatLocal is running on
	 */
	public void registerPlatform(MultiChatLocalPlatform platform) {
		this.platform = platform;
	}

	/**
	 * Get the platform currently being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The platform being used
	 */
	public MultiChatLocalPlatform getPlatform() {
		if (this.platform == null) throw new IllegalStateException("MultiChatLocal platform has not been registered");
		return this.platform;
	}

	/**
	 * Register the name of the MultiChatLocal plugin (i.e. MultiChatSpigot etc.)
	 * 
	 * <p>It is important that this matches the name in plugin.yml or equivalent!</p>
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param pluginName The name of the MultiChatLocal plugin
	 */
	public void registerPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	/**
	 * Get the name of the MultiChatLocal plugin (i.e. MultiChatSpigot, MultiChatSponge ...)
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * s
	 * @return The name of the MultiChatLocal plugin
	 */
	public String getPluginName() {
		if (this.pluginName == null) throw new IllegalStateException("MultiChatLocal plugin name has not been registered");
		return this.pluginName;
	}

	/**
	 * Register the config directory being used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param configDirectory The config directory being used by MultiChatLocal
	 */
	public void registerConfigDirectory(File configDirectory) {
		this.configDirectory = configDirectory;
	}

	/**
	 * Get the config directory currently being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The config directory being used
	 */
	public File getConfigDirectory() {
		if (this.configDirectory == null) throw new IllegalStateException("MultiChatLocal config directory has not been registered");
		return this.configDirectory;
	}

	/**
	 * Register the name manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param nameManager The name manager to register to the API
	 */
	public void registerNameManager(LocalNameManager nameManager) {
		this.nameManager = nameManager;
	}

	/**
	 * Get the name manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The local name manager
	 */
	public LocalNameManager getNameManager() {
		if (this.nameManager == null) throw new IllegalStateException("No MultiChat local name manager has been registered");
		return this.nameManager;
	}

	/**
	 * Register the config manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param configManager The config manager to register to the API
	 */
	public void registerConfigManager(LocalConfigManager configManager) {
		this.configManager = configManager;
	}

	/**
	 * Get the config manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The local config manager
	 */
	public LocalConfigManager getConfigManager() {
		if (this.configManager == null) throw new IllegalStateException("No MultiChat local config manager has been registered");
		return this.configManager;
	}

	/**
	 * Register the local data store to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param dataStore The local data store to register to the API
	 */
	public void registerDataStore(LocalDataStore dataStore) {
		this.dataStore = dataStore;
	}

	/**
	 * Get the local data store being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The local data store
	 */
	public LocalDataStore getDataStore() {
		if (this.dataStore == null) throw new IllegalStateException("No MultiChat local data store has been registered");
		return this.dataStore;
	}

	/**
	 * Register the meta manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param metaManager The meta manager to register to the API
	 */
	public void registerMetaManager(LocalMetaManager metaManager) {
		this.metaManager = metaManager;
	}

	/**
	 * Get the meta manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The local meta manager
	 */
	public LocalMetaManager getMetaManager() {
		if (this.metaManager == null) throw new IllegalStateException("No MultiChat local meta manager has been registered");
		return this.metaManager;
	}

	/**
	 * Register the proxy communication manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param proxyCommunicationManager The Proxy Communication manager to register to the API
	 */
	public void registerProxyCommunicationManager(LocalProxyCommunicationManager proxyCommunicationManager) {
		this.proxyCommunicationManager = proxyCommunicationManager;
	}

	/**
	 * Get the proxy communication manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The proxy communication manager registered with the API
	 */
	public LocalProxyCommunicationManager getProxyCommunicationManager() {
		if (this.proxyCommunicationManager == null) throw new IllegalStateException("No MultiChat proxy communication manager has been registered");
		return this.proxyCommunicationManager;
	}

	/**
	 * Register the file system manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param fileSystemManager The File System manager to register to the API
	 */
	public void registerFileSystemManager(LocalFileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
	}

	/**
	 * Get the file system manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The file system manager registered with the API
	 */
	public LocalFileSystemManager getFileSystemManager() {
		if (this.fileSystemManager == null) throw new IllegalStateException("No MultiChat file system manager has been registered");
		return this.fileSystemManager;
	}

	/**
	 * Register the console logger to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param consoleLogger The local console logger to register to the API
	 */
	public void registerConsoleLogger(LocalConsoleLogger consoleLogger) {
		this.consoleLogger = consoleLogger;
	}

	/**
	 * Get the console logger being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The console logger registered with the API
	 */
	public LocalConsoleLogger getConsoleLogger() {
		if (this.consoleLogger == null) throw new IllegalStateException("No MultiChat console logger has been registered");
		return this.consoleLogger;
	}

	/**
	 * Register the placeholder manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param placeholderManager The local placeholder manager to register to the API
	 */
	public void registerPlaceholderManager(LocalPlaceholderManager placeholderManager) {
		this.placeholderManager = placeholderManager;
	}

	/**
	 * Get the placeholder manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The placeholder manager registered with the API
	 */
	public LocalPlaceholderManager getPlaceholderManager() {
		if (this.placeholderManager == null) throw new IllegalStateException("No MultiChat placeholder manager has been registered");
		return this.placeholderManager;
	}

	/**
	 * Register the chat manager to be used by MultiChatLocal
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param chatManager The local chat manager to register to the API
	 */
	public void registerChatManager(LocalChatManager chatManager) {
		this.chatManager = chatManager;
	}

	/**
	 * Get the chat manager being used by MultiChatLocal
	 * 
	 * <p>Will throw Illegal State Exception if one has not been registered</p>
	 * 
	 * @return The chat manager registered with the API
	 */
	public LocalChatManager getChatManager() {
		if (this.chatManager == null) throw new IllegalStateException("No MultiChat chat manager has been registered");
		return this.chatManager;
	}

}
