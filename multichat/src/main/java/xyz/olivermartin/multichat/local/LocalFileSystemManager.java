package xyz.olivermartin.multichat.local;

import java.io.File;

public class LocalFileSystemManager {

	private LocalNicknameFile nicknameFile;

	public LocalFileSystemManager() {
		/* EMPTY */
	}

	/**
	 * Register the local nicknames data file with the Local File System Manager
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param platform The platform MultiChatLocal is using (Spigot, Sponge etc.)
	 * @param fileName filename i.e. namedata.dat
	 * @param configPath THE PATH WITHOUT THE FILE NAME
	 */
	public void registerNicknameFile(MultiChatLocalPlatform platform, String fileName, File configPath) {

		switch (platform) {
		case SPIGOT:
			nicknameFile = new LocalSpigotNicknameFile(configPath, fileName);
			break;
		case SPONGE:
			nicknameFile = new LocalSpongeNicknameFile(configPath, fileName);
			break;
		default:
			throw new IllegalArgumentException("Could not register file because this type of platform is not allowed.");

		}

	}

	public LocalNicknameFile getNicknameFile() {

		if (nicknameFile == null) throw new IllegalStateException("No local nickname file has been registered");
		return nicknameFile;

	}

}
