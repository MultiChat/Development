package xyz.olivermartin.multichat.proxy.common.store;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyNameFileDataStore extends ProxyAbstractFileDataStore implements ProxyNameDataStore{

	// MAP PLAYER UUIDS TO THEIR LAST NAMES
	private Map<UUID, String> lastNames;

	public ProxyNameFileDataStore(MultiChatProxyPlatform platform, File path, String filename) {
		super(platform, path, filename);
	}

	@Override
	public boolean existsUUID(UUID uuid) {
		return lastNames.containsKey(uuid);
	}

	@Override
	public Optional<String> getLastName(UUID uuid) {
		if (lastNames.containsKey(uuid)) return Optional.of(lastNames.get(uuid));
		return Optional.empty();
	}

	@Override
	public void setLastName(UUID uuid, String lastName) {
		lastNames.put(uuid, lastName);
	}

	protected void setLastNames(Map<UUID, String> lastNames) {
		this.lastNames = lastNames;
	}

	protected Map<UUID, String> getLastNames() {
		return this.lastNames;
	}

}
