package xyz.olivermartin.multichat.proxy.common.store;

import java.util.Optional;

import xyz.olivermartin.multichat.common.DataStoreMode;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyDataStoreManager {

	private MultiChatProxyPlatform platform;
	private DataStoreMode mode;

	private ProxyChannelsDataStore channelsDataStore;
	private ProxyNameDataStore nameDataStore;

	public ProxyDataStoreManager(MultiChatProxyPlatform platform, DataStoreMode mode) {
		this.platform = platform;
		this.mode = mode;
	}

	public MultiChatProxyPlatform getPlatform() {
		return this.platform;
	}

	public DataStoreMode getMode() {
		return this.mode;
	}

	protected void registerChannelsDataStore(ProxyChannelsDataStore channelsDataStore) {
		this.channelsDataStore = channelsDataStore;
	}

	public Optional<ProxyChannelsDataStore> getChannelsDataStore() {
		if (channelsDataStore != null) return Optional.of(channelsDataStore);
		return Optional.empty();
	}

	protected void registerNameDataStore(ProxyNameDataStore nameDataStore) {
		this.nameDataStore = nameDataStore;
	}

	public Optional<ProxyNameDataStore> getNameDataStore() {
		if (nameDataStore != null) return Optional.of(nameDataStore);
		return Optional.empty();
	}

}
