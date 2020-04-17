package xyz.olivermartin.multichat.local.common.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;

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

	/**
	 * Is global chat enabled for this server?
	 */
	public boolean globalChatServer = false;

	/**
	 * The format to be used for Global chat received from the proxy
	 */
	public String globalChatFormat = "&f%DISPLAYNAME%&f: ";

	/**
	 * Chat Queues to handle the local global hack
	 */
	public Map<String, Queue<String>> chatQueues = new HashMap<String, Queue<String>>();

	/**
	 * What channel is each player speaking in?
	 */
	public Map<UUID, String> playerChannels = new HashMap<UUID, String>();

	/**
	 * Which players can chat using colours
	 */
	public Map<UUID, Boolean> colourMap = new HashMap<UUID, Boolean>();

	/**
	 * List of channels with their members
	 */
	public Map<String, LocalPseudoChannel> channelObjects = new HashMap<String, LocalPseudoChannel>();

	/**
	 * Map of who players ignore
	 */
	public Map<UUID, Set<UUID>> ignoreMap = new HashMap<UUID, Set<UUID>>();

}
