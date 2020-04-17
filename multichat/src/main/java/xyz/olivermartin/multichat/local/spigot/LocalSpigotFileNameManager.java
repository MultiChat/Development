package xyz.olivermartin.multichat.local.spigot;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.storage.LocalFileNameManager;

public class LocalSpigotFileNameManager extends LocalFileNameManager {

	public LocalSpigotFileNameManager() {
		super(MultiChatLocalPlatform.SPIGOT);
	}

}
