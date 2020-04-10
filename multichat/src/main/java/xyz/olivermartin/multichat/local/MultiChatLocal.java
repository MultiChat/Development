package xyz.olivermartin.multichat.local;

import java.io.File;

import xyz.olivermartin.multichat.local.communication.LocalProxyCommunicationManager;

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
	private LocalMetaManager metaManager;
	private LocalProxyCommunicationManager proxyCommunicationManager;

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

}
