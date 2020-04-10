package xyz.olivermartin.multichat.local;

import java.util.UUID;

public abstract class LocalMetaManager {

	public LocalMetaManager() { /* EMPTY */ }
	
	public String getNick(UUID uuid) {
		return MultiChatLocal.getInstance().getNameManager().getCurrentName(uuid);
	}
	
	public abstract String getPrefix(UUID uuid);
	
	public abstract String getSuffix(UUID uuid);
	
	public abstract String getWorld(UUID uuid);
	
	public abstract String getDisplayName(UUID uuid);
	
}
