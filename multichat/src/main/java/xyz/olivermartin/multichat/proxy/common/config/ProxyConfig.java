package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.List;

import xyz.olivermartin.multichat.common.config.ConfigStatus;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyConfig {

	// FILE SETTINGS
	private File configPath;
	private String fileName;
	protected boolean ready;
	private MultiChatProxyPlatform platform;

	public ProxyConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		this.configPath = configPath;
		this.fileName = fileName;
		this.platform = platform;
		ConfigStatus startupStatus = startupConfig();
		ready = (startupStatus != ConfigStatus.FAILED);
	}

	public MultiChatProxyPlatform getPlatform() {
		return this.platform;
	}

	protected File getFile() {
		return new File(configPath, fileName);
	}

	protected ConfigStatus startupConfig() {

		boolean createdNew = false;

		try {

			File file = new File(configPath, fileName);

			if (!file.exists()) {
				saveDefaultConfig();
				createdNew = true;
			} 

			ConfigStatus loadStatus = loadConfig();
			if (loadStatus == ConfigStatus.FAILED) return ConfigStatus.FAILED;

			setMemberAttributes();

			if (createdNew) {
				return ConfigStatus.CREATED;
			} else {
				return ConfigStatus.LOADED;
			}

		} catch (IOException e) {
			return ConfigStatus.FAILED;
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
	protected abstract ConfigStatus loadConfig();

	/**
	 * This should set all the member attributes of this file according to the real file (or to a default value if not present)
	 */
	protected abstract void setMemberAttributes();

	protected abstract String getString(String configNode, String defaultValue);

	protected abstract boolean getBoolean(String configNode, boolean defaultValue);

	protected abstract int getInt(String configNode, int defaultValue);

	protected abstract List<String> getStringList(String configNode);

	/**
	 * Reload this configuration file (and reload all the member attributes)
	 */
	public ConfigStatus reload() {

		ConfigStatus startupStatus = startupConfig();
		return startupStatus;

	}

}
