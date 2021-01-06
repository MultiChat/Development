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
	private boolean setDisplayName = false;

	/**
	 * The format for MultiChatLocal to use if it is setting display names.
	 */
	private String displayNameFormatLastVal = "%PREFIX%%NICK%%SUFFIX%";

	/**
	 * Is global chat enabled for this server?
	 */
	private boolean globalChatServer = false;

	/**
	 * The format to be used for Global chat received from the proxy
	 */
	private String globalChatFormat = "&f%DISPLAYNAME%&f: ";

	/**
	 * Channel formats received from the proxy
	 */
	private Map<String, String> channelFormats = new HashMap<String, String>();

	/**
	 * Chat Queues to handle the local global hack
	 */
	private Map<String, Queue<String>> chatQueues = new HashMap<String, Queue<String>>();

	/**
	 * Recipient Queues
	 */
	private Map<UUID, Queue<Set<UUID>>> recipientQueues = new HashMap<UUID, Queue<Set<UUID>>>();

	/**
	 * What channel is each player speaking in?
	 */
	private Map<UUID, String> playerChannels = new HashMap<UUID, String>();

	/**
	 * Which players can chat using colours
	 */
	private Map<UUID, Boolean> simpleColourMap = new HashMap<UUID, Boolean>();

	/**
	 * Which players can chat using rgb colours
	 */
	private Map<UUID, Boolean> rgbColourMap = new HashMap<UUID, Boolean>();

	/**
	 * List of channels with their members
	 */
	private Map<String, LocalPseudoChannel> channelObjects = new HashMap<String, LocalPseudoChannel>();

	/**
	 * IS THIS A LEGACY SERVER THAT NEEDS RGB CODE APPROX.?
	 */
	private boolean legacy = false;

	/**
	 * @return the setDisplayName
	 */
	public synchronized boolean isSetDisplayName() {
		return setDisplayName;
	}

	/**
	 * @return the displayNameFormatLastVal
	 */
	public synchronized String getDisplayNameFormatLastVal() {
		return displayNameFormatLastVal;
	}

	/**
	 * @return the globalChatServer
	 */
	public synchronized boolean isGlobalChatServer() {
		return globalChatServer;
	}

	/**
	 * @return the globalChatFormat
	 */
	public synchronized String getGlobalChatFormat() {
		return globalChatFormat;
	}

	/**
	 * @return the chatQueues
	 */
	public synchronized Map<String, Queue<String>> getChatQueues() {
		return chatQueues;
	}

	/**
	 * @return the recipientQueues
	 */
	public synchronized Map<UUID, Queue<Set<UUID>>> getRecipientQueues() {
		return recipientQueues;
	}

	/**
	 * @return the playerChannels
	 */
	public synchronized Map<UUID, String> getPlayerChannels() {
		return playerChannels;
	}

	/**
	 * @return the colourMap
	 */
	public synchronized Map<UUID, Boolean> getSimpleColourMap() {
		return simpleColourMap;
	}

	/**
	 * @return the rgbMap
	 */
	public synchronized Map<UUID, Boolean> getRGBColourMap() {
		return rgbColourMap;
	}

	/**
	 * @return the channelObjects
	 */
	public synchronized Map<String, LocalPseudoChannel> getChannelObjects() {
		return channelObjects;
	}

	/**
	 * @return the legacy
	 */
	public synchronized boolean isLegacy() {
		return legacy;
	}

	/**
	 * @param setDisplayName the setDisplayName to set
	 */
	public synchronized void setSetDisplayName(boolean setDisplayName) {
		this.setDisplayName = setDisplayName;
	}

	/**
	 * @param displayNameFormatLastVal the displayNameFormatLastVal to set
	 */
	public synchronized void setDisplayNameFormatLastVal(String displayNameFormatLastVal) {
		this.displayNameFormatLastVal = displayNameFormatLastVal;
	}

	/**
	 * @param globalChatServer the globalChatServer to set
	 */
	public synchronized void setGlobalChatServer(boolean globalChatServer) {
		this.globalChatServer = globalChatServer;
	}

	/**
	 * @param globalChatFormat the globalChatFormat to set
	 */
	public synchronized void setGlobalChatFormat(String globalChatFormat) {
		this.globalChatFormat = globalChatFormat;
	}

	/**
	 * @param chatQueues the chatQueues to set
	 */
	public synchronized void setChatQueues(Map<String, Queue<String>> chatQueues) {
		this.chatQueues = chatQueues;
	}

	/**
	 * @param recipientQueues the recipientQueues to set
	 */
	public synchronized void setRecipientQueues(Map<UUID, Queue<Set<UUID>>> recipientQueues) {
		this.recipientQueues = recipientQueues;
	}

	/**
	 * @param playerChannels the playerChannels to set
	 */
	public synchronized void setPlayerChannels(Map<UUID, String> playerChannels) {
		this.playerChannels = playerChannels;
	}

	/**
	 * @param colourMap the colourMap to set
	 */
	public synchronized void setSimpleColourMap(Map<UUID, Boolean> simpleColourMap) {
		this.simpleColourMap = simpleColourMap;
	}

	/**
	 * @param colourMap the colourMap to set
	 */
	public synchronized void setRGBColourMap(Map<UUID, Boolean> rgbColourMap) {
		this.rgbColourMap = rgbColourMap;
	}

	/**
	 * @param channelObjects the channelObjects to set
	 */
	public synchronized void setChannelObjects(Map<String, LocalPseudoChannel> channelObjects) {
		this.channelObjects = channelObjects;
	}

	/**
	 * @param legach the legacy to set
	 */
	public synchronized void setLegacy(boolean legacy) {
		this.legacy = legacy;
	}

	/**
	 * @return the channelFormats
	 */
	public Map<String, String> getChannelFormats() {
		return channelFormats;
	}

	/**
	 * @param channelFormats the channelFormats to set
	 */
	public void setChannelFormats(Map<String, String> channelFormats) {
		this.channelFormats = channelFormats;
	}

}
