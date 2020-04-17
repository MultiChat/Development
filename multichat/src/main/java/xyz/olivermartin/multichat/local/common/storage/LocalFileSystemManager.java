package xyz.olivermartin.multichat.local.common.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.spigot.LocalSpigotNicknameFile;
import xyz.olivermartin.multichat.local.sponge.LocalSpongeNicknameFile;

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
	public void registerNicknameFile(MultiChatLocalPlatform platform, String fileName, File configPath, LocalFileNameManager lfnm) {

		switch (platform) {
		case SPIGOT:
			nicknameFile = new LocalSpigotNicknameFile(configPath, fileName, lfnm);
			break;
		case SPONGE:
			nicknameFile = new LocalSpongeNicknameFile(configPath, fileName, lfnm);
			break;
		default:
			throw new IllegalArgumentException("Could not register file because this type of platform is not allowed.");

		}

	}

	public LocalNicknameFile getNicknameFile() {

		if (nicknameFile == null) throw new IllegalStateException("No local nickname file has been registered");
		return nicknameFile;

	}

	public boolean createResource(String fileName, File destination) {

		// Load default file into input stream
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

		// Copy to desired location
		try {
			Files.copy(inputStream, new File(destination, fileName).toPath(), new CopyOption[0]);
			return true;
		} catch (IOException e) {
			return false;
		}

	}

}
