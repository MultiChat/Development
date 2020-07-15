package xyz.olivermartin.multichat.proxy.common;

/**
 * A proxy data store of settings and other things for MultiChatProxy
 * 
 * <p>These may be updated due to messages received from the local servers</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyDataStore {

	private boolean chatFrozen;

	public synchronized boolean isChatFrozen() {
		return this.chatFrozen;
	}

	public synchronized void setChatFrozen(boolean frozen) {
		this.chatFrozen = frozen;
	}

}
