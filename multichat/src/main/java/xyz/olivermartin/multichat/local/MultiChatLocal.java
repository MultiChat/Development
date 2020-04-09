package xyz.olivermartin.multichat.local;

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
	private LocalNameManager nameManager;
	private LocalConfigManager configManager;

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
	 * Register the config to be used by MultiChatLocal
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

}
