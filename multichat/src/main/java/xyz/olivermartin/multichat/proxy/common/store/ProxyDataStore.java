package xyz.olivermartin.multichat.proxy.common.store;

import xyz.olivermartin.multichat.common.DataStoreMode;

public abstract class ProxyDataStore {

	private DataStoreMode mode;

	public ProxyDataStore(DataStoreMode mode) {
		this.mode = mode;
	}

	public DataStoreMode getMode() {
		return this.mode;
	}

}
