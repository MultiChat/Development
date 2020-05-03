package xyz.olivermartin.multichat.proxy.common.store;

import java.io.File;

import xyz.olivermartin.multichat.common.DataStoreMode;
import xyz.olivermartin.multichat.proxy.bungee.store.ProxyBungeeChannelsFileDataStore;
import xyz.olivermartin.multichat.proxy.bungee.store.ProxyBungeeNameFileDataStore;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public class ProxyFileDataStoreManager extends ProxyDataStoreManager {

	public ProxyFileDataStoreManager(MultiChatProxyPlatform platform) {
		super(platform, DataStoreMode.FILE);
	}

	public void registerChannelsFileDataStore(File path, String filename) {

		switch (getPlatform()) {

		case BUNGEE:
		default:
			registerChannelsDataStore(new ProxyBungeeChannelsFileDataStore(path, filename));
			break;

		}

	}

	public void registerNameFileDataStore(File path, String filename) {

		switch (getPlatform()) {

		case BUNGEE:
		default:
			registerNameDataStore(new ProxyBungeeNameFileDataStore(path, filename));
			break;

		}

	}

}
