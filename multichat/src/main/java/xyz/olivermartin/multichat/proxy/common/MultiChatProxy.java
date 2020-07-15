package xyz.olivermartin.multichat.proxy.common;

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

	private ProxyDataStore dataStore;

	/* END ATTRIBUTES */

	private MultiChatProxy() { /* EMPTY */ }

	public ProxyDataStore getDataStore() {
		return this.dataStore;
	}

	public void registerDataStore(ProxyDataStore dataStore) {
		this.dataStore = dataStore;
	}

}
