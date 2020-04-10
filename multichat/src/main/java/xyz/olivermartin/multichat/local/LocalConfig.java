package xyz.olivermartin.multichat.local;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public abstract class LocalConfig {

	// SERVER SETTINGS
	public String serverName;

	// GLOBAL CHAT SETTINGS
	public boolean overrideGlobalFormat;
	public String overrideGlobalFormatFormat;
	public boolean overrideAllMultiChatFormatting;
	public boolean forceMultiChatFormat;

	// LOCAL CHAT SETTINGS
	public boolean setLocalFormat;
	public String localChatFormat;

	// PLACEHOLDER SETTINGS
	public Map<String,String> multichatPlaceholders;

	// NICKNAME SETTINGS
	public List<String> nicknameBlacklist;
	public boolean showNicknamePrefix;
	public String nicknamePrefix;
	public int nicknameLengthLimit;
	public int nicknameLengthMin;
	public boolean nicknameLengthLimitFormatting;
	public boolean nicknameSQL;
	public boolean mySQL;


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
	private void setMemberAttributes() {

		// Server
		serverName = getString("server_name","SPIGOT_SERVER");

		// Global Chat
		overrideGlobalFormat = getBoolean("override_global_format", false);
		overrideGlobalFormatFormat = getString("override_global_format_format", "&5[&dG&5] &f%DISPLAYNAME%&f: ");
		overrideAllMultiChatFormatting = getBoolean("override_all_multichat_formatting", false);
		forceMultiChatFormat = getBoolean("force_multichat_format", false);

		// Local Chat
		setLocalFormat = getBoolean("set_local_format", true);
		localChatFormat = getString("local_chat_format", "&3[&bL&3] &f%DISPLAYNAME%&f: &7");

		// Placeholders
		multichatPlaceholders = getPlaceholdersMap("multichat_placeholders");

		// Nicknames
		nicknameBlacklist = getStringList("nickname_blacklist");
		showNicknamePrefix = getBoolean("show_nickname_prefix",false);
		nicknamePrefix = getString("nickname_prefix","~");
		nicknameLengthLimit = getInt("nickname_length_limit", 20);
		nicknameLengthMin = getInt("nickname_length_min", 3);
		nicknameLengthLimitFormatting = getBoolean("nickname_length_limit_formatting", false);
		nicknameSQL = getBoolean("nickname_sql", false);

		// MySQL
		mySQL = getBoolean("mysql", false);
		LocalDatabaseCredentials.getInstance().updateValues(getString("mysql_url",""),
				getString("mysql_database",""),
				getString("mysql_user",""),
				getString("mysql_pass",""));

	}

	protected abstract String getString(String configNode, String defaultValue);

	protected abstract boolean getBoolean(String configNode, boolean defaultValue);

	protected abstract int getInt(String configNode, int defaultValue);

	protected abstract List<String> getStringList(String configNode);

	protected abstract Map<String,String> getPlaceholdersMap(String rootConfigNode);

	/**
	 * Reload this configuration file (and reload all the member attributes)
	 */
	public LocalConfigStatus reload() {

		LocalConfigStatus startupStatus = startupConfig();
		return startupStatus;

	}
}
