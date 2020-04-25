package xyz.olivermartin.multichat.proxy.common.store;

import xyz.olivermartin.multichat.common.DataStoreMode;

public interface ProxyDataStore {

	/**
	 * Get the mode of this data store
	 * @return
	 */
	public DataStoreMode getMode();

	/**
	 * Is this data store ready to be used?
	 * @return
	 */
	public boolean isReady();

	/**
	 * Reload the data into the store from the file / database. This overwrites anything that wasn't saved.
	 * @return
	 */
	public boolean reload();

	/**
	 * This saves the data from the store into the file / database.
	 * @return
	 */
	public boolean save();

}
