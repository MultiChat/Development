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

	// TODO Add attributes

	/* END ATTRIBUTES */

	private MultiChatProxy() { /* EMPTY */ }

}
