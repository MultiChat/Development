package xyz.olivermartin.multichat.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public abstract class LocalConfig {

	// NICKNAME SETTINGS
	public boolean showNicknamePrefix;
	public String nicknamePrefix;

	// FILE SETTINGS
	private File configPath;
	private String fileName;
	protected boolean ready;
	private MultiChatLocalPlatform platform;

	public LocalConfig(File configPath, String fileName, MultiChatLocalPlatform platform) {
		this.configPath = configPath;
		this.fileName = fileName;
		this.platform = platform;
		LocalConfigStatus startupStatus = startupConfig();
		ready = (startupStatus != LocalConfigStatus.FAILED);
	}

	public MultiChatLocalPlatform getPlatform() {
		return this.platform;
	}

	protected File getFile() {
		return new File(configPath, fileName);
	}

	protected LocalConfigStatus startupConfig() {

		boolean createdNew = false;

		try {

			File file = new File(configPath, fileName);

			if (!file.exists()) {
				saveDefaultConfig();
				createdNew = true;
			} 

			LocalConfigStatus loadStatus = loadConfig();
			if (loadStatus == LocalConfigStatus.FAILED) return LocalConfigStatus.FAILED;

			setMemberAttributes();

			if (createdNew) {
				return LocalConfigStatus.CREATED;
			} else {
				return LocalConfigStatus.LOADED;
			}

		} catch (IOException e) {
			return LocalConfigStatus.FAILED;
		}

	}

	protected void saveDefaultConfig() throws IOException {

		// Load default file into input stream
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

		// Copy to desired location
		Files.copy(inputStream, new File(configPath, fileName).toPath(), new CopyOption[0]);

	}

	/**
	 * This should physically load the config from the file system into some kind of Configuration class depending on the platform
	 * @return Status of loading config
	 */
	protected abstract LocalConfigStatus loadConfig();

	/**
	 * This should set all the member attributes of this file according to the real file (or to a default value if not present)
	 */
	protected abstract void setMemberAttributes();

	/**
	 * Reload this configuration file (and reload all the member attributes)
	 */
	public LocalConfigStatus reload() {

		LocalConfigStatus startupStatus = startupConfig();
		return startupStatus;

	}
}
