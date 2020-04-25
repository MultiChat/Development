package xyz.olivermartin.multichat.proxy.common.store;

import xyz.olivermartin.multichat.common.DataStoreMode;

public abstract class ProxyAbstractDataStore implements ProxyDataStore {

	private DataStoreMode mode;
	private boolean ready;

	public ProxyAbstractDataStore(DataStoreMode mode) {
		this.mode = mode;
		this.ready = this.init();
	}

	@Override
	public DataStoreMode getMode() {
		return this.mode;
	}

	@Override
	public boolean isReady() {
		return this.ready;
	}

	/**
	 * Initialise data store.
	 * 
	 * <p>This routine is called automatically on object construction</p>
	 * 
	 * <p>This should load all values ready to be accessed.</p>
	 * 
	 * <p>If it is a file then it should make sure it exists and create it if it doesn't...</p>
	 * <p>If it is a database then it should check the tables exist and create them if they don't...</p>
	 * 
	 * @return True if it successfully initialised, false otherwise
	 */
	protected abstract boolean init();

}
