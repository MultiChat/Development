package xyz.olivermartin.multichat.proxy.common;

import java.io.File;

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
	private File configDirectory;
	private ProxyDataStore dataStore;
	private ProxyFileStoreManager fileStoreManager;

	/* END ATTRIBUTES */

	private MultiChatProxy() { /* EMPTY */ }

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

}
