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
	private String serverName;

	// GLOBAL CHAT SETTINGS
	private boolean overrideGlobalFormat;
	private String overrideGlobalFormatFormat;
	private boolean overrideAllMultiChatFormatting;
	private boolean forceMultiChatFormat;

	// LOCAL CHAT SETTINGS
	private boolean setLocalFormat;
	private String localChatFormat;

	// PLACEHOLDER SETTINGS
	private Map<String,String> multichatPlaceholders;

	// NICKNAME SETTINGS
	private List<String> nicknameBlacklist;
	private boolean showNicknamePrefix;
	private String nicknamePrefix;
	private int nicknameLengthLimit;
	private int nicknameLengthMin;
	private boolean nicknameLengthLimitFormatting;
	private boolean nicknameSQL;
	private boolean mySQL;


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

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @return the overrideGlobalFormat
	 */
	public boolean isOverrideGlobalFormat() {
		return overrideGlobalFormat;
	}

	/**
	 * @return the overrideGlobalFormatFormat
	 */
	public String getOverrideGlobalFormatFormat() {
		return overrideGlobalFormatFormat;
	}

	/**
	 * @return the overrideAllMultiChatFormatting
	 */
	public boolean isOverrideAllMultiChatFormatting() {
		return overrideAllMultiChatFormatting;
	}

	/**
	 * @return the forceMultiChatFormat
	 */
	public boolean isForceMultiChatFormat() {
		return forceMultiChatFormat;
	}

	/**
	 * @return the setLocalFormat
	 */
	public boolean isSetLocalFormat() {
		return setLocalFormat;
	}

	/**
	 * @return the localChatFormat
	 */
	public String getLocalChatFormat() {
		return localChatFormat;
	}

	/**
	 * @return the multichatPlaceholders
	 */
	public Map<String, String> getMultichatPlaceholders() {
		return multichatPlaceholders;
	}

	/**
	 * @return the nicknameBlacklist
	 */
	public List<String> getNicknameBlacklist() {
		return nicknameBlacklist;
	}

	/**
	 * @return the showNicknamePrefix
	 */
	public boolean isShowNicknamePrefix() {
		return showNicknamePrefix;
	}

	/**
	 * @return the nicknamePrefix
	 */
	public String getNicknamePrefix() {
		return nicknamePrefix;
	}

	/**
	 * @return the nicknameLengthLimit
	 */
	public int getNicknameLengthLimit() {
		return nicknameLengthLimit;
	}

	/**
	 * @return the nicknameLengthMin
	 */
	public int getNicknameLengthMin() {
		return nicknameLengthMin;
	}

	/**
	 * @return the nicknameLengthLimitFormatting
	 */
	public boolean isNicknameLengthLimitFormatting() {
		return nicknameLengthLimitFormatting;
	}

	/**
	 * @return the nicknameSQL
	 */
	public boolean isNicknameSQL() {
		return nicknameSQL;
	}

	/**
	 * @return the mySQL
	 */
	public boolean isMySQL() {
		return mySQL;
	}
}
