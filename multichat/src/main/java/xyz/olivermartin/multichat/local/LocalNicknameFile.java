package xyz.olivermartin.multichat.local;

import java.io.File;

public abstract class LocalNicknameFile {

	// FILE SETTINGS
	private File configPath;
	private String fileName;
	protected boolean ready;
	private MultiChatLocalPlatform platform;

	public LocalNicknameFile(File configPath, String fileName, MultiChatLocalPlatform platform) {
		this.configPath = configPath;
		this.fileName = fileName;
		this.platform = platform;
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

		File file = new File(configPath, fileName);

		boolean status;

		if (!file.exists()) {
			status = saveFile(file);
		}

		status = loadFile(file);

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
