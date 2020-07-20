package xyz.olivermartin.multichat.common.communication;

public class CommChannels {

	/*
	 * The prefix used for multichat communication channels
	 */
	private static String prefix = "multichat:";

	// Player meta communication (prefix, suffix, displayname etc.)
	private static String playerMeta = "pmeta";

	// Chat messages sent by players
	private static String playerChat = "pchat";

	// Chat messages sent by non-player objects (casts, announcements etc.)
	private static String serverChat = "schat";

	// Data about specific players (selected channels, colour permissions etc.)
	private static String playerData = "pdata";

	// Data about the server as a whole (ignoremap, global chat servers, display name formats, legacy servers etc.)
	private static String serverData = "sdata";

	// Actions for the server console (mce/pxe)
	private static String serverAction = "sact";

	// Actions for players (mce players, ppxe)
	private static String playerAction = "pact";

	/**
	 * Gets the channel id used for: 
	 * <b>PLAYER META</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 *  <li>Requests for player meta to be updated on proxy</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>Player prefix</li>
	 *  <li>Player suffix</li>
	 *  <li>Player world</li>
	 *  <li>Player display name</li>
	 *  <li>Player nickname</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getPlayerMeta() {
		return prefix + playerMeta;
	}

	/**
	 * Gets the channel id used for: 
	 * <b>PLAYER CHAT</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 * 	<li>Direct chat messages through /local mymessagehere etc.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>Player chat messages for distribution</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getPlayerChat() {
		return prefix + playerChat;
	}

	/**
	 * Gets the channel id used for: 
	 * <b>SERVER CHAT</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 * 	<li>Cast messages</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>Nil.</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getServerChat() {
		return prefix + serverChat;
	}

	/**
	 * Gets the channel id used for: 
	 * <b>PLAYER DATA</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 * 	<li>Player colour permissions</li>
	 *  <li>Player currently selected channels</li>
	 *  <li>Lists of channel members</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>Nil.</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getPlayerData() {
		return prefix + playerData;
	}

	/**
	 * Gets the channel id used for: 
	 * <b>SERVER DATA</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 * 	<li>Player ignore map</li>
	 *  <li>Global chat format</li>
	 *  <li>If this server is a 'global chat server'</li>
	 *  <li>If this server is a legacy server</li>
	 * 	<li>If the server should set the display name</li>
	 *  <li>What the display name format is</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>Nil.</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getServerData() {
		return prefix + serverData;
	}

	/**
	 * Gets the channel id used for: 
	 * <b>SERVER ACTIONS</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 * 	<li>MCE commands</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>PXE commands</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getServerAction() {
		return prefix + serverAction;
	}

	/**
	 * Gets the channel id used for: 
	 * <b>PLAYER ACTIONS</b>
	 * 
	 * <p><b>Description: </b>
	 * <p>
	 * This channel communicates the following from PROXY -> LOCAL:
	 * <ul>
	 * 	<li>MCE player commands</li>
	 * </ul>
	 * </p>
	 * <p>
	 * This channel communicates the following from LOCAL -> PROXY:
	 * <ul>
	 * 	<li>PXE player commands</li>
	 * </ul>
	 * </p>
	 * </p>
	 * @return the channel id
	 */
	public static String getPlayerAction() {
		return prefix + playerAction;
	}

}
