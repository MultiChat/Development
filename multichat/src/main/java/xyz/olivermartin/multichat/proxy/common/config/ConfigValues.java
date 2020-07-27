package xyz.olivermartin.multichat.proxy.common.config;

public class ConfigValues {

	public static interface Config {

		// Version

		/**
		 * STRING :: Version of config file
		 */
		String VERSION = "version";

		// General

		/**
		 * BOOL :: Should display names be fetched from spigot
		 */
		String FETCH_SPIGOT_DISPLAY_NAMES = "fetch_spigot_display_names";

		/**
		 * BOOL :: Should MultiChat set display names
		 */
		String SET_DISPLAY_NAME = "set_display_name";

		/**
		 * STRING :: The format MultiChat should use to set display names
		 */
		String DISPLAY_NAME_FORMAT = "display_name_format";

		// Private Messaging

		/**
		 * BOOL :: Should private messaging be enabled
		 */
		String PM = "pm";

		/**
		 * STRING LIST :: List of servers that are excluded from PMs
		 */
		String NO_PM = "no_pm";

		/**
		 * BOOL :: Can players toggle pms with /msg playername?
		 */
		String TOGGLE_PM = "toggle_pm";

		/**
		 * STRING :: Format used for outgoing PMs
		 */
		String PM_OUT_FORMAT = "pmout";

		/**
		 * STRING :: Format used for incoming PMs
		 */
		String PM_IN_FORMAT = "pmin";

		/**
		 * STRING :: Format used for social spy PMs
		 */
		String PM_SPY_FORMAT = "pmspy";

		// Chat Channels

		/**
		 * STRING :: Default channel (local or global)
		 */
		String DEFAULT_CHANNEL = "default_channel";

		/**
		 * BOOL :: Should the default channel be enforced every time a player joins?
		 */
		String FORCE_CHANNEL_ON_JOIN = "force_channel_on_join";

		// Global Chat

		/**
		 * BOOL :: Should global chat be enabled?
		 */
		String GLOBAL = "global";

		/**
		 * STRING LIST :: List of servers to be excluded from global chat
		 */
		String NO_GLOBAL = "no_global";

		/**
		 * STRING :: Format for global chat
		 */
		String GLOBAL_FORMAT = "globalformat";

		// Group Chats

		public static interface GroupChat {

			/**
			 * The prefix for this section of the config file
			 */
			String PREFIX = "groupchat.";

			/**
			 * STRING :: Format for group chats
			 */
			String FORMAT = PREFIX + "format";

			/**
			 * STRING :: Default chat colour
			 */
			String CC_DEFAULT = PREFIX + "ccdefault";

			/**
			 * STRING :: Default name colour
			 */
			String NC_DEFAULT = PREFIX + "ncdefault";

		}

		// Staff Chats

		public static interface ModChat {

			/**
			 * The prefix for this section of the config file
			 */
			String PREFIX = "modchat.";

			/**
			 * STRING :: Format for mod chat
			 */
			String FORMAT = PREFIX + "format";

			/**
			 * STRING :: Default chat colour
			 */
			String CC_DEFAULT = PREFIX + "ccdefault";

			/**
			 * STRING :: Default name colour
			 */
			String NC_DEFAULT = PREFIX + "ncdefault";

		}

		public static interface AdminChat {

			/**
			 * The prefix for this section of the config file
			 */
			String PREFIX = "adminchat.";

			/**
			 * STRING :: Format for admin chat
			 */
			String FORMAT = PREFIX + "format";

			/**
			 * STRING :: Default chat colour
			 */
			String CC_DEFAULT = PREFIX + "ccdefault";

			/**
			 * STRING :: Default name colour
			 */
			String NC_DEFAULT = PREFIX + "ncdefault";

		}

		// Other Settings

		/**
		 * BOOL :: Should staff list be enabled?
		 */
		String STAFF_LIST = "staff_list";

		public static interface PrivacySettings {

			/**
			 * The prefix for this section of the config file
			 */
			String PREFIX = "privacy_settings.";

			/**
			 * BOOL :: Should PMs be logged?
			 */
			String LOG_PMS = PREFIX + "log_pms";

			/**
			 * BOOL :: Should staff chat be logged?
			 */
			String LOG_STAFFCHAT = PREFIX + "log_staffchat";

			/**
			 * BOOL :: Should group chat be logged?
			 */
			String LOG_GROUPCHAT = PREFIX + "log_groupchat";

		}

		public static interface PremiumVanish {

			/**
			 * The prefix for this section of the config file
			 */
			String PREFIX = "premium_vanish.";

			/**
			 * BOOL :: Should PMs to vanished staff be prevented?
			 */
			String PREVENT_MESSAGE = PREFIX + "prevent_message";

			/**
			 * BOOL :: Should vanished staff be hidden from staff list?
			 */
			String PREVENT_STAFF_LIST = PREFIX + "prevent_staff_list";

			/**
			 * BOOL :: Should join messages be hidden for vanished staff?
			 */
			String SILENCE_JOIN = PREFIX + "silence_join";

		}

		/**
		 * STRING LIST :: List of pre 1.16 servers for RGB code approximation
		 */
		String LEGACY_SERVERS = "legacy_servers";

	}

}
