package xyz.olivermartin.multichat.proxy.common.store;

import java.util.Optional;
import java.util.UUID;

public interface ProxyNameDataStore {

	public boolean existsUUID(UUID uuid);

	public Optional<String> getLastName(UUID uuid);

	public void setLastName(UUID uuid, String lastName);

}
