package xyz.olivermartin.multichat.proxy.common.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProxyFileStoreManager {

	Map<String, ProxyFileStore> fileStores;

	public ProxyFileStoreManager() {
		this.fileStores = new HashMap<String, ProxyFileStore>();
	}

	public void registerFileStore(String id, ProxyFileStore fileStore) {
		this.fileStores.put(id.toLowerCase(), fileStore);
	}

	public void unregisterFileStore(String id) {
		this.fileStores.remove(id.toLowerCase());
	}

	public Optional<ProxyFileStore> getFileStore(String id) {
		if (!fileStores.containsKey(id.toLowerCase())) return Optional.empty();
		return Optional.of(fileStores.get(id.toLowerCase()));
	}

	public void reload() {
		for (ProxyFileStore fileStore : fileStores.values()) {
			fileStore.reload();
		}
	}

	public void save() {
		for (ProxyFileStore fileStore : fileStores.values()) {
			fileStore.save();
		}
	}

}
