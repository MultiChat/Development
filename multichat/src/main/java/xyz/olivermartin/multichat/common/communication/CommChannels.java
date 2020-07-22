package xyz.olivermartin.multichat.common.communication;

public interface CommChannels {

	/*
	 * The prefix used for multichat communication channels
	 */
	String PREFIX = "multichat:";

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
	String PLAYER_META = PREFIX + "pmeta";

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
	String PLAYER_CHAT = PREFIX + "pchat";

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
	String SERVER_CHAT = PREFIX + "schat";

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
	String PLAYER_DATA = PREFIX + "pdata";

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
	String SERVER_DATA = PREFIX + "sdata";

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
	String SERVER_ACTION = PREFIX + "sact";

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
	String PLAYER_ACTION = PREFIX + "pact";

}
