package xyz.olivermartin.multichat.proxy.common.config;

public enum ConfigFile {

	CONFIG ("config.yml"),
	JOIN_MESSAGES ("joinmessages.yml"),
	CHAT_CONTROL ("chatcontrol.yml"),
	MESSAGES ("messages.yml"),
	ALIASES ("aliases.yml");

	private String fileName;

	private ConfigFile(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Get the raw file name of this config file
	 * @return the file name
	 */
	public String getFileName() {
		return this.fileName;
	}

}
