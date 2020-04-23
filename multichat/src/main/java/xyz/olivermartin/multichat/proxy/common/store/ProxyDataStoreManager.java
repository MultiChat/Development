package xyz.olivermartin.multichat.proxy.common.store;

import java.util.Optional;

import xyz.olivermartin.multichat.common.DataStoreMode;

public class ProxyDataStoreManager {

	private DataStoreMode mode;

	private ProxyChannelsDataStore channelsDataStore;

	public ProxyDataStoreManager(DataStoreMode mode) {
		this.mode = mode;
	}

	public DataStoreMode getMode() {
		return this.mode;
	}

	public void registerChannelsDataStore() {

		switch (mode) {
		case SQL:

			// TODO

			break;
		case FILE:
		default:
			channelsDataStore = new ProxyChannelsFileDataStore();
			break;
		}

	}

	public Optional<ProxyChannelsDataStore> getChannelsDataStore() {
		if (channelsDataStore != null) return Optional.of(channelsDataStore);
		return Optional.empty();
	}

}
