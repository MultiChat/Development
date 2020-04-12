package xyz.olivermartin.multichat.local.storage;

/**
 * A local data store of settings and other things for MultiChatLocal
 * 
 * <p>Often these will be updated due to messages received from the proxy</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class LocalDataStore {

	/**
	 * Should MultiChatLocal set the display name of players?
	 */
	public boolean setDisplayName = false;
	
	/**
	 * The format for MultiChatLocal to use if it is setting display names.
	 */
	public String displayNameFormatLastVal = "%PREFIX%%NICK%%SUFFIX%";
	
}
