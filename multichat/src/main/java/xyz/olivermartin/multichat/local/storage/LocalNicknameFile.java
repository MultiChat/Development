package xyz.olivermartin.multichat.local.storage;

import java.io.File;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

public abstract class LocalNicknameFile {

	// FILE SETTINGS
	private File configPath;
	private String fileName;
	protected boolean ready;
	private MultiChatLocalPlatform platform;
	protected LocalFileNameManager lfnm;

	public LocalNicknameFile(File configPath, String fileName, LocalFileNameManager lfnm, MultiChatLocalPlatform platform) {
		this.configPath = configPath;
		this.fileName = fileName;
		this.platform = platform;
		this.lfnm = lfnm;
		ready = startupFile();
	}

	public MultiChatLocalPlatform getPlatform() {
		return this.platform;
	}

	protected File getFile() {
		return new File(configPath, fileName);
	}

	/**
	 * Save the nickname data to file
	 * @return
	 */
	public boolean save() {
		File file = new File(configPath, fileName);
		return saveFile(file);
	}

	/**
	 * Load the nickname data from the file into the system (overwrites anything currently loaded)
	 * @return
	 */
	public boolean reload() {
		return startupFile();
	}

	protected boolean startupFile() {
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalNicknameFile] Starting up file...");

		File file = new File(configPath, fileName);

		boolean status;

		if (!file.exists()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalNicknameFile] File does not already exist...");
			status = saveFile(file);
		}

		status = loadFile(file);
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalNicknameFile] FINAL STATUS = " + status);

		return status;

	}

	/**
	 * Load the file contents into the name manager
	 * @param file
	 * @return
	 */
	protected abstract boolean loadFile(File file);

	/**
	 * Save the file contents from the name manager
	 * @param file
	 * @return
	 */
	protected abstract boolean saveFile(File file);

}
