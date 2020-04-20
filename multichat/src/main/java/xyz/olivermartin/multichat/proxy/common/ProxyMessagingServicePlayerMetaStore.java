package xyz.olivermartin.multichat.proxy.common;

import java.util.UUID;

public class ProxyMessagingServicePlayerMetaStore extends ProxyPlayerMetaStore {

	public ProxyMessagingServicePlayerMetaStore() {
		super(ProxyPlayerMetaStoreMode.MESSAGING_SERVICE);
	}

	@Override
	public String getPrefix(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void offerPrefix(UUID uuid, String prefix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSuffix(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void offerSuffix(UUID uuid, String suffix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getWorld(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void offerWorld(UUID uuid, String world) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCurrentName(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void offerCurrentName(UUID uuid, String currentName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void offerName(UUID uuid, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDisplayName(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void offerDisplayName(UUID uuid, String displayName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerPlayer(UUID uuid, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterPlayer(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

}
