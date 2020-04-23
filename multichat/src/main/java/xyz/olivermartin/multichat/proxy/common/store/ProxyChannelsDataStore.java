package xyz.olivermartin.multichat.proxy.common.store;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class ProxyChannelsDataStore {

	public abstract Optional<String> getCurrentChannel(UUID uuid);

	public abstract void setCurrentChannel(UUID uuid, String id);

	public abstract Set<UUID> getHiddenViewers(String id);

	public abstract boolean hasHiddenChannel(UUID uuid, String channelId);

	public abstract void hideChannel(UUID uuid, String id);

	public abstract void showChannel(UUID uuid, String id);

}
