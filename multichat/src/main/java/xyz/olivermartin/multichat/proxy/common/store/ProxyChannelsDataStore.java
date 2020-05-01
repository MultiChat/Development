package xyz.olivermartin.multichat.proxy.common.store;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProxyChannelsDataStore extends ProxyDataStore {

	public Optional<String> getCurrentChannel(UUID uuid);

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
