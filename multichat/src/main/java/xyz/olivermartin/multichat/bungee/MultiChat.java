package xyz.olivermartin.multichat.bungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;


/**
 * The MAIN MultiChat Class
 * <p>This class is the main plugin. All plugin enable and disable control happens here.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChat extends Plugin implements Listener {

	public static final String LATEST_VERSION = "1.7.3";

	public static final String[] ALLOWED_VERSIONS = new String[] {

			LATEST_VERSION,
			"1.7.2",
			"1.7.1",
			"1.7",
			"1.6.2",
			"1.6.1",
			"1.6",
			"1.5.2",
			"1.5.1",
			"1.5",
			"1.4.2",
			"1.4.1",
			"1.4",
			"1.3.4",
			"1.3.3",
			"1.3.2",
			"1.3.1",
			"1.3"

	};

	public static Map<UUID, TChatInfo> modchatpreferences = new HashMap<UUID, TChatInfo>();
	public static Map<UUID, TChatInfo> adminchatpreferences = new HashMap<UUID, TChatInfo>();
	public static Map<String, TGroupChatInfo> groupchats = new HashMap<String, TGroupChatInfo>();

	public static Map<UUID, String> viewedchats = new HashMap<UUID, String>();
	public static Map<UUID, UUID> lastmsg = new HashMap<UUID, UUID>();
	public static List<UUID> allspy = new ArrayList<UUID>();
	public static List<UUID> socialspy = new ArrayList<UUID>();

	public static File configDir;
	public static String configversion;

	public static boolean frozen;

	public static String defaultChannel = "";
	public static boolean forceChannelOnJoin = false;

	private static MultiChat instance;

	public static MultiChat getInstance() {
		return instance;
	}

	public void backup() {

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				getLogger().info("Commencing backup!");

				saveChatInfo();
				saveGroupChatInfo();
				saveGroupSpyInfo();
				saveGlobalChatInfo();
				saveSocialSpyInfo();
				saveAnnouncements();
				saveBulletins();
				saveCasts();
				saveMute();
				saveIgnore();
				UUIDNameManager.saveUUIDS();

				getLogger().info("Backup complete. Any errors reported above.");

			}

		}, 1L, 60L, TimeUnit.MINUTES);

	}

	public void fetchDisplayNames() {

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {

					getProxy();
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
					}

				}

			}

		}, 1L, 5L, TimeUnit.MINUTES);

	}

	@EventHandler
	public void onLogin(PostLoginEvent event) {

		fetchDisplayNameOnce(event.getPlayer().getName());

	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {

		fetchDisplayNameOnce(event.getPlayer().getName());

	}

	public void fetchDisplayNameOnce(final String playername) {

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}
				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 0L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}
				}

				catch (NullPointerException ex) { /* EMPTY */ }
			}

		}, 1L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}

				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 2L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}

				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 4L, TimeUnit.SECONDS);

	}

	public void onEnable() {

		instance = this;

		configDir = getDataFolder();
		if (!getDataFolder().exists()) {
			System.out.println("[MultiChat] Creating plugin directory!");
			getDataFolder().mkdirs();
		}

		String translationsDir = configDir.toString() + File.separator + "translations";
		if (!new File(translationsDir).exists()) {
			System.out.println("[MultiChat] Creating translations directory!");
			new File(translationsDir).mkdirs();
		}

		ConfigManager.getInstance().registerHandler("config.yml", configDir);
		ConfigManager.getInstance().registerHandler("joinmessages.yml", configDir);
		ConfigManager.getInstance().registerHandler("messages.yml", configDir);
		ConfigManager.getInstance().registerHandler("chatcontrol.yml", configDir);

		ConfigManager.getInstance().registerHandler("messages_fr.yml", new File(translationsDir));
		ConfigManager.getInstance().registerHandler("joinmessages_fr.yml", new File(translationsDir));

		Configuration configYML = ConfigManager.getInstance().getHandler("config.yml").getConfig();
		Configuration chatcontrolYML = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		configversion = configYML.getString("version");

		if (Arrays.asList(ALLOWED_VERSIONS).contains(configversion)) {

			// TODO - Remove for future versions!
			if (!configversion.equals(LATEST_VERSION))  {

				getLogger().info("[!!!] [WARNING] YOUR CONFIG FILES ARE NOT THE LATEST VERSION");
				getLogger().info("[!!!] [WARNING] MULTICHAT 1.7 INTRODUCES SEVERAL NEW FEATURES WHICH ARE NOT IN YOUR OLD FILE");
				getLogger().info("[!!!] [WARNING] THE PLUGIN SHOULD WORK WITH THE OLDER FILE, BUT IS NOT SUPPORTED!");
				getLogger().info("[!!!] [WARNING] PLEASE BACKUP YOUR OLD CONFIG FILES AND DELETE THEM FROM THE MULTICHAT FOLDER SO NEW ONES CAN BE GENERATED!");
				getLogger().info("[!!!] [WARNING] THANK YOU");

			}

			// Register listeners
			getProxy().getPluginManager().registerListener(this, new Events());
			getProxy().getPluginManager().registerListener(this, this);

			// Register communication channels and appropriate listeners
			getProxy().registerChannel("multichat:comm");
			getProxy().registerChannel("multichat:prefix");
			getProxy().registerChannel("multichat:suffix");
			getProxy().registerChannel("multichat:nick");
			getProxy().registerChannel("multichat:world");
			getProxy().registerChannel("multichat:action");
			getProxy().registerChannel("multichat:paction");
			getProxy().registerChannel("multichat:chat");
			getProxy().registerChannel("multichat:channel");
			getProxy().registerChannel("multichat:ignore");
			getProxy().getPluginManager().registerListener(this, new BungeeComm());

			// Register commands
			registerCommands(configYML, chatcontrolYML);

			System.out.println("[MultiChat] Config Version: " + configversion);

			// Run start-up routines
			Startup();
			UUIDNameManager.Startup();
			
			// Set up chat control stuff
			if (chatcontrolYML.contains("link_control")) {
				ChatControl.controlLinks = chatcontrolYML.getBoolean("link_control");
				ChatControl.linkMessage = chatcontrolYML.getString("link_removal_message");
			}

			// Set default channel
			defaultChannel = configYML.getString("default_channel");
			forceChannelOnJoin = configYML.getBoolean("force_channel_on_join");

			// Set up global chat
			GlobalChannel channel = Channel.getGlobalChannel();
			channel.setFormat(configYML.getString("globalformat"));

			// Add all appropriate servers to this hardcoded global chat stream
			for (String server : configYML.getStringList("no_global")) {
				channel.addServer(server);
			}

			// Initiate backup routine
			backup();

			// Fetch display names of all players
			fetchDisplayNames();

		} else {
			getLogger().info("Config incorrect version! Please repair or delete it!");
		}
	}

	public void onDisable() {

		getLogger().info("Thankyou for using MultiChat. Disabling...");

		saveChatInfo();
		saveGroupChatInfo();
		saveGroupSpyInfo();
		saveGlobalChatInfo();
		saveSocialSpyInfo();
		saveAnnouncements();
		saveBulletins();
		saveCasts();
		saveMute();
		saveIgnore();
		UUIDNameManager.saveUUIDS();

	}

	public void registerCommands(Configuration configYML, Configuration chatcontrolYML) {

		// Register main commands
		getProxy().getPluginManager().registerCommand(this, CommandManager.getAcc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getAc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMcc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getGc());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getGroup());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getGrouplist());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMultichat());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMultichatBypass());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getMultiChatExecute());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getDisplay());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getFreezechat());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getHelpme());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getClearchat());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getAnnouncement());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getBulletin());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getCast());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getUsecast());
		getProxy().getPluginManager().registerCommand(this, CommandManager.getIgnore());

		// Register PM commands
		if (configYML.getBoolean("pm")) {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getMsg());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getReply());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getSocialspy());
		}

		// Register global chat commands
		if (configYML.getBoolean("global")) {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getLocal());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getGlobal());
			getProxy().getPluginManager().registerCommand(this, CommandManager.getChannel());
		}

		// Register staff list command /staff
		if (configYML.contains("staff_list")) {
			if (configYML.getBoolean("staff_list")) {
				getProxy().getPluginManager().registerCommand(this, CommandManager.getStafflist());
			}
		} else {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getStafflist());
		}

		// Register mute command
		if (chatcontrolYML.getBoolean("mute")) {
			getProxy().getPluginManager().registerCommand(this, CommandManager.getMute());
		}

	}

	public void unregisterCommands(Configuration configYML, Configuration chatcontrolYML) {

		// Unregister main commands
		getProxy().getPluginManager().unregisterCommand(CommandManager.getAcc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getAc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMcc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getGc());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getGroup());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getGrouplist());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMultichat());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMultichatBypass());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getMultiChatExecute());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getDisplay());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getFreezechat());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getHelpme());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getClearchat());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getAnnouncement());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getBulletin());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getCast());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getUsecast());
		getProxy().getPluginManager().unregisterCommand(CommandManager.getIgnore());

		// Unregister PM commands
		if (configYML.getBoolean("pm")) {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getMsg());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getReply());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getSocialspy());
		}

		// Unregister global chat commands
		if (configYML.getBoolean("global")) {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getLocal());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getGlobal());
			getProxy().getPluginManager().unregisterCommand(CommandManager.getChannel());
		}

		// Unregister staff list command /staff
		if (configYML.contains("staff_list")) {
			if (configYML.getBoolean("staff_list")) {
				getProxy().getPluginManager().unregisterCommand(CommandManager.getStafflist());
			}
		} else {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getStafflist());
		}

		// UnRegister mute command
		if (chatcontrolYML.getBoolean("mute")) {
			getProxy().getPluginManager().unregisterCommand(CommandManager.getMute());
		}

	}

	public static void saveAnnouncements() {

		try {
			File file = new File(configDir, "Announcements.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(Announcements.getAnnouncementList());
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the announcements file!");
			e.printStackTrace();
		}

	}

	public static void saveBulletins() {

		try	{
			File file = new File(configDir, "Bulletins.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeBoolean(Bulletins.isEnabled());
			out.writeInt(Bulletins.getTimeBetween());
			out.writeObject(Bulletins.getArrayList());
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the bulletins file!");
			e.printStackTrace();
		}

	}

	public static void saveChatInfo() {

		try {
			File file = new File(configDir, "StaffChatInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(modchatpreferences);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the mod chat info file!");
			e.printStackTrace();
		}

		try {
			File file = new File(configDir, "AdminChatInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(adminchatpreferences);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the admin chat info file!");
			e.printStackTrace();
		}

	}

	public static void saveGroupChatInfo() {

		try {
			File file = new File(configDir, "GroupChatInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(groupchats);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the group chat info file!");
			e.printStackTrace();
		}

	}

	public static void saveCasts() {

		try {
			File file = new File(configDir, "Casts.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(CastControl.castList);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the casts file!");
			e.printStackTrace();
		}

	}

	public static void saveGroupSpyInfo() {

		try {
			File file = new File(configDir, "GroupSpyInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(allspy);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the group spy info file!");
			e.printStackTrace();
		}

	}

	public static void saveSocialSpyInfo() {

		try {
			File file = new File(configDir, "SocialSpyInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(socialspy);
			out.close();
		} catch (IOException e)	{
			System.out.println("[MultiChat] [Save Error] An error has occured writing the social spy info file!");
			e.printStackTrace();
		}

	}

	public static void saveGlobalChatInfo() {

		try {
			File file = new File(configDir, "GlobalChatInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(ChatModeManager.getInstance().getData());
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the global chat info file!");
			e.printStackTrace();
		}

	}

	public static void saveMute() {

		try {
			File file = new File(configDir, "Mute.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(ChatControl.getMutedPlayers());
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the mute file!");
			e.printStackTrace();
		}

	}

	public static void saveIgnore() {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (config.getBoolean("session_ignore")) return;

		try {
			File file = new File(configDir, "Ignore.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(ChatControl.getIgnoreMap());
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the ignore file!");
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static HashMap<UUID, TChatInfo> loadModChatInfo() {

		HashMap<UUID, TChatInfo> result = null;

		try {
			File file = new File(configDir, "StaffChatInfo.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<UUID, TChatInfo>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the mod chat info file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static void loadBulletins() {

		ArrayList<String> result = null;
		boolean enabled = false;
		int timeBetween = 0;

		try {
			File file = new File(configDir, "Bulletins.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			enabled = in.readBoolean();
			timeBetween = in.readInt();
			result = (ArrayList<String>)in.readObject();
			in.close();
			Bulletins.setArrayList(result);
			if (enabled) {
				Bulletins.startBulletins(timeBetween);
			}
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the bulletins file!");
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> loadAnnouncements() {

		HashMap<String, String> result = null;

		try {
			File file = new File(configDir, "Announcements.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<String, String>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the announcements file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static HashMap<UUID, TChatInfo> loadAdminChatInfo() {

		HashMap<UUID, TChatInfo> result = null;

		try {
			File file = new File(configDir, "AdminChatInfo.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<UUID, TChatInfo>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the admin chat info file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> loadCasts() {

		HashMap<String, String> result = null;

		try	{
			File file = new File(configDir, "Casts.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<String, String>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the casts file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, TGroupChatInfo> loadGroupChatInfo() {

		HashMap<String, TGroupChatInfo> result = null;

		try {
			File file = new File(configDir, "GroupChatInfo.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<String, TGroupChatInfo>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the group chat info file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static List<UUID> loadGroupSpyInfo() {

		List<UUID> result = null;

		try {
			File file = new File(configDir, "GroupSpyInfo.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (List<UUID>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the group spy info file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static List<UUID> loadSocialSpyInfo() {

		List<UUID> result = null;

		try {
			File file = new File(configDir, "SocialSpyInfo.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (List<UUID>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the social spy info file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static Map<UUID, Boolean> loadGlobalChatInfo() {

		Map<UUID, Boolean> result = null;

		try {
			File file = new File(configDir, "GlobalChatInfo.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (Map<UUID, Boolean>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the global chat info file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static Set<UUID> loadMute() {

		Set<UUID> result = null;

		try {
			File file = new File(configDir, "Mute.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (Set<UUID>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the mute file!");
			e.printStackTrace();
		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public static Map<UUID, Set<UUID>> loadIgnore() {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (config.getBoolean("session_ignore")) return new HashMap<UUID, Set<UUID>>();

		Map<UUID, Set<UUID>> result = null;

		try {
			File file = new File(configDir, "Ignore.dat");
			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (Map<UUID, Set<UUID>>)in.readObject();
			in.close();
		} catch (IOException|ClassNotFoundException e) {
			System.out.println("[MultiChat] [Load Error] An error has occured reading the ignore file!");
			e.printStackTrace();
		}

		return result;

	}

	public static void Startup() {

		System.out.println("[MultiChat] Starting load routine for data files");

		File f = new File(configDir, "StaffChatInfo.dat");
		File f2 = new File(configDir, "AdminChatInfo.dat");

		if ((f.exists()) && (!f.isDirectory()) && (f2.exists()) && (!f2.isDirectory())) {

			modchatpreferences.putAll(loadModChatInfo());
			adminchatpreferences.putAll(loadAdminChatInfo());

		} else {

			System.out.println("[MultiChat] Some staff chat files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveChatInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f3 = new File(configDir, "GroupChatInfo.dat");

		if ((f3.exists()) && (!f3.isDirectory())) {

			groupchats.putAll(loadGroupChatInfo());

		} else {

			System.out.println("[MultiChat] Some group chat files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Group Chats! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGroupChatInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f4 = new File(configDir, "GroupSpyInfo.dat");

		if ((f4.exists()) && (!f4.isDirectory())) {

			allspy = loadGroupSpyInfo();

		} else {

			System.out.println("[MultiChat] Some group spy files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Group-Spy! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGroupSpyInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f5 = new File(configDir, "GlobalChatInfo.dat");

		if ((f5.exists()) && (!f5.isDirectory())) {

			ChatModeManager.getInstance().loadData(loadGlobalChatInfo());

		} else {

			System.out.println("[MultiChat] Some global chat files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Global Chat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGlobalChatInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f6 = new File(configDir, "SocialSpyInfo.dat");

		if ((f6.exists()) && (!f6.isDirectory())) {

			socialspy = loadSocialSpyInfo();

		} else {

			System.out.println("[MultiChat] Some social spy files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Social Spy! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGroupSpyInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f7 = new File(configDir, "Announcements.dat");

		if ((f7.exists()) && (!f7.isDirectory())) {

			Announcements.loadAnnouncementList((loadAnnouncements()));

		} else {

			System.out.println("[MultiChat] Some announcements files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveAnnouncements();
			System.out.println("[MultiChat] The files were created!");

		}

		File f8 = new File(configDir, "Bulletins.dat");

		if ((f8.exists()) && (!f8.isDirectory())) {

			loadBulletins();

		} else {

			System.out.println("[MultiChat] Some bulletins files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveBulletins();
			System.out.println("[MultiChat] The files were created!");

		}

		File f9 = new File(configDir, "Casts.dat");

		if ((f9.exists()) && (!f9.isDirectory())) {

			CastControl.castList = loadCasts();

		} else {

			System.out.println("[MultiChat] Some casts files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveCasts();
			System.out.println("[MultiChat] The files were created!");

		}

		File f10 = new File(configDir, "Mute.dat");

		if ((f10.exists()) && (!f10.isDirectory())) {

			ChatControl.setMutedPlayers(loadMute());

		} else {

			System.out.println("[MultiChat] Some mute files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveMute();
			System.out.println("[MultiChat] The files were created!");

		}

		File f11 = new File(configDir, "Ignore.dat");

		if ((f11.exists()) && (!f11.isDirectory())) {

			ChatControl.setIgnoreMap(loadIgnore());

		} else {

			System.out.println("[MultiChat] Some ignore files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveMute();
			System.out.println("[MultiChat] The files were created!");

		}

		System.out.println("[MultiChat] [COMPLETE] Load sequence finished! (Any errors reported above)");

	}
}
