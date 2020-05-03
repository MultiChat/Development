package xyz.olivermartin.multichat.proxy.common.store;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;

public interface ProxyChannelsDataStore extends ProxyDataStore {

	/**
	 * Get the current channel id in its user abstracted form.
	 * 
	 * <p>I.e. local channel is just "local", and not its true "local:server" form.</p>
	 * 
	 * @param uuid The user's UUID
	 * @return Their channel ID, if they are in local channel this will just return "local". THIS CHANNEL ID CANNOT BE USED TO GET A CHANNEL OBJECT!
	 */
	public Optional<String> getAbstractedCurrentChannel(UUID uuid);

	/**
	 * Get the fully correct current channel id
	 * 
	 * <p>I.e. local channel will return its true "local:server" form.</p>
	 * 
	 * @param uuid The user's UUID
	 * @return Their channel ID which can then be used to get a channel object
	 */
	public Optional<String> getCorrectCurrentChannel(MultiChatProxyPlayer player);

	/**
	 * Set the current channel for the user.
	 * <p>Note local should be set as just "local", not its full form of "local:server"</p>
	 * @param uuid
	 * @param id
	 */
	public void setCurrentChannel(UUID uuid, String id);



	public Set<UUID> getHiddenViewers(String id);

	public boolean hasHiddenChannel(UUID uuid, String channelId);

	public void hideChannel(UUID uuid, String id);

	public void showChannel(UUID uuid, String id);



	public void addToJoinedChannelList(UUID uuid, String id);

	public void removeFromJoinedChannelList(UUID uuid, String id);

	public Set<UUID> getJoinedChannelList(String id);

	public boolean isInJoinedChannelList(UUID uuid, String id);

}
