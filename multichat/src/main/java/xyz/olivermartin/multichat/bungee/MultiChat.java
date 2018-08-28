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
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.bungee.commands.ACCCommand;
import xyz.olivermartin.multichat.bungee.commands.ACCommand;
import xyz.olivermartin.multichat.bungee.commands.AnnouncementCommand;
import xyz.olivermartin.multichat.bungee.commands.BulletinCommand;
import xyz.olivermartin.multichat.bungee.commands.CastCommand;
import xyz.olivermartin.multichat.bungee.commands.ClearChatCommand;
import xyz.olivermartin.multichat.bungee.commands.DisplayCommand;
import xyz.olivermartin.multichat.bungee.commands.FreezeChatCommand;
import xyz.olivermartin.multichat.bungee.commands.GCCommand;
import xyz.olivermartin.multichat.bungee.commands.GlobalCommand;
import xyz.olivermartin.multichat.bungee.commands.GroupCommand;
import xyz.olivermartin.multichat.bungee.commands.GroupListCommand;
import xyz.olivermartin.multichat.bungee.commands.HelpMeCommand;
import xyz.olivermartin.multichat.bungee.commands.LocalCommand;
import xyz.olivermartin.multichat.bungee.commands.MCCCommand;
import xyz.olivermartin.multichat.bungee.commands.MCCommand;
import xyz.olivermartin.multichat.bungee.commands.MsgCommand;
import xyz.olivermartin.multichat.bungee.commands.MultiChatCommand;
import xyz.olivermartin.multichat.bungee.commands.ReplyCommand;
import xyz.olivermartin.multichat.bungee.commands.SocialSpyCommand;
import xyz.olivermartin.multichat.bungee.commands.StaffListCommand;
import xyz.olivermartin.multichat.bungee.commands.UseCastCommand;

// NAME IDEAS: Backchat, Totalk, Talkative, Portalk, Netalk, Revtalkr, Chatplex, Talky, Photalk

/**
 * The MAIN MultiChat Class
 * <p>This class is the main plugin. All plugin enable and disable control happens here.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChat extends Plugin implements Listener {

	public static final String LATEST_VERSION = "1.6";

	public static final String[] ALLOWED_VERSIONS = new String[] {

			// TODO REMOVE OLD VERSIONS / CREATE UPDATER FROM 1.5.2 TO 1.6

			LATEST_VERSION,
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

	public static File ConfigDir;
	public static String configversion;

	public static ConfigManager configman = new ConfigManager();
	public static JMConfigManager jmconfigman = new JMConfigManager();

	public static Map<UUID, Boolean> globalplayers = new HashMap<UUID, Boolean>();

	public static boolean frozen;

	public static ChatStream globalChat;

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
				UUIDNameManager.saveUUIDS();

				getLogger().info("Backup complete. Any errors reported above.");

			}

		}, 1L, 60L, TimeUnit.MINUTES);

	}

	public void fetchDisplayNames() {

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				if (configman.config.getBoolean("fetch_spigot_display_names") == true) {

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

					if (configman.config.getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}
				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 0L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (configman.config.getBoolean("fetch_spigot_display_names") == true) {

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

					if (configman.config.getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}

				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 2L, TimeUnit.SECONDS);

		getProxy().getScheduler().schedule(this, new Runnable() {

			public void run() {

				try {

					if (configman.config.getBoolean("fetch_spigot_display_names") == true) {

						ProxiedPlayer player = getProxy().getPlayer(playername);
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());

					}

				} catch (NullPointerException ex) { /* EMPTY */ }

			}

		}, 4L, TimeUnit.SECONDS);

	}

	public void onEnable() {

		instance = this;

		ConfigDir = getDataFolder();
		if (!getDataFolder().exists()) {
			System.out.println("[MultiChat] Creating plugin directory!");
			getDataFolder().mkdirs();
		}

		configman.startupConfig();
		jmconfigman.startupConfig();

		configversion = configman.config.getString("version");

		if (Arrays.asList(ALLOWED_VERSIONS).contains(configversion)) {

			// Register listeners
			getProxy().getPluginManager().registerListener(this, new Events());
			getProxy().getPluginManager().registerListener(this, this);

			// Register main commands
			getProxy().getPluginManager().registerCommand(this, new MCCommand());
			getProxy().getPluginManager().registerCommand(this, new ACCommand());
			getProxy().getPluginManager().registerCommand(this, new MCCCommand());
			getProxy().getPluginManager().registerCommand(this, new ACCCommand());
			getProxy().getPluginManager().registerCommand(this, new GCCommand());
			getProxy().getPluginManager().registerCommand(this, new GroupCommand());
			getProxy().getPluginManager().registerCommand(this, new StaffListCommand());
			getProxy().getPluginManager().registerCommand(this, new GroupListCommand());
			getProxy().getPluginManager().registerCommand(this, new MultiChatCommand());
			getProxy().getPluginManager().registerCommand(this, new DisplayCommand());
			getProxy().getPluginManager().registerCommand(this, new FreezeChatCommand());
			getProxy().getPluginManager().registerCommand(this, new HelpMeCommand());
			getProxy().getPluginManager().registerCommand(this, new ClearChatCommand());
			getProxy().getPluginManager().registerCommand(this, new AnnouncementCommand());
			getProxy().getPluginManager().registerCommand(this, new BulletinCommand());
			getProxy().getPluginManager().registerCommand(this, new CastCommand());
			getProxy().getPluginManager().registerCommand(this, new UseCastCommand());

			// Register communication channels and appropriate listeners
			getProxy().registerChannel("multichat:comm");
			getProxy().getPluginManager().registerListener(this, new BungeeComm());

			// Register PM commands
			if (configman.config.getBoolean("pm")) {
				getProxy().getPluginManager().registerCommand(this, new MsgCommand());
				getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
				getProxy().getPluginManager().registerCommand(this, new SocialSpyCommand());
			}

			// Register global chat commands
			if (configman.config.getBoolean("global")) {
				getProxy().getPluginManager().registerCommand(this, new LocalCommand());
				getProxy().getPluginManager().registerCommand(this, new GlobalCommand());
			}

			System.out.println("[MultiChat] Config Version: " + configversion);

			// Run start-up routines
			Startup();
			UUIDNameManager.Startup();

			//TODO REPLACE THIS... Create hard-coded global chat stream
			globalChat = new ChatStream("GLOBAL", configman.config.getString("globalformat"), false, false);

			// Add all appropriate servers to this hardcoded global chat stream
			for (String server : configman.config.getStringList("no_global")) {
				globalChat.addServer(server);
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
		UUIDNameManager.saveUUIDS();

	}

	public static void saveAnnouncements() {

		try {
			File file = new File(ConfigDir, "Announcements.dat");
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
			File file = new File(ConfigDir, "Bulletins.dat");
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
			File file = new File(ConfigDir, "StaffChatInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(modchatpreferences);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the mod chat info file!");
			e.printStackTrace();
		}

		try {
			File file = new File(ConfigDir, "AdminChatInfo.dat");
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
			File file = new File(ConfigDir, "GroupChatInfo.dat");
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
			File file = new File(ConfigDir, "Casts.dat");
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
			File file = new File(ConfigDir, "GroupSpyInfo.dat");
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
			File file = new File(ConfigDir, "SocialSpyInfo.dat");
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
			File file = new File(ConfigDir, "GlobalChatInfo.dat");
			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(globalplayers);
			out.close();
		} catch (IOException e) {
			System.out.println("[MultiChat] [Save Error] An error has occured writing the global chat info file!");
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static HashMap<UUID, TChatInfo> loadModChatInfo() {

		HashMap<UUID, TChatInfo> result = null;

		try {
			File file = new File(ConfigDir, "StaffChatInfo.dat");
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
			File file = new File(ConfigDir, "Bulletins.dat");
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
			File file = new File(ConfigDir, "Announcements.dat");
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
			File file = new File(ConfigDir, "AdminChatInfo.dat");
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
			File file = new File(ConfigDir, "Casts.dat");
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
			File file = new File(ConfigDir, "GroupChatInfo.dat");
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
			File file = new File(ConfigDir, "GroupSpyInfo.dat");
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
			File file = new File(ConfigDir, "SocialSpyInfo.dat");
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
			File file = new File(ConfigDir, "GlobalChatInfo.dat");
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

	public static void Startup() {

		System.out.println("[MultiChat] Starting load routine for data files");

		File f = new File(ConfigDir, "StaffChatInfo.dat");
		File f2 = new File(ConfigDir, "AdminChatInfo.dat");

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

		File f3 = new File(ConfigDir, "GroupChatInfo.dat");

		if ((f3.exists()) && (!f3.isDirectory())) {

			groupchats.putAll(loadGroupChatInfo());

		} else {

			System.out.println("[MultiChat] Some group chat files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Group Chats! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGroupChatInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f4 = new File(ConfigDir, "GroupSpyInfo.dat");

		if ((f4.exists()) && (!f4.isDirectory())) {

			allspy = loadGroupSpyInfo();

		} else {

			System.out.println("[MultiChat] Some group spy files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Group-Spy! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGroupSpyInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f5 = new File(ConfigDir, "GlobalChatInfo.dat");

		if ((f5.exists()) && (!f5.isDirectory())) {

			globalplayers = loadGlobalChatInfo();

		} else {

			System.out.println("[MultiChat] Some global chat files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Global Chat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGlobalChatInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f6 = new File(ConfigDir, "SocialSpyInfo.dat");

		if ((f6.exists()) && (!f6.isDirectory())) {

			socialspy = loadSocialSpyInfo();

		} else {

			System.out.println("[MultiChat] Some social spy files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Enabling Social Spy! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveGroupSpyInfo();
			System.out.println("[MultiChat] The files were created!");

		}

		File f7 = new File(ConfigDir, "Announcements.dat");

		if ((f7.exists()) && (!f7.isDirectory())) {

			Announcements.loadAnnouncementList((loadAnnouncements()));

		} else {

			System.out.println("[MultiChat] Some announcements files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveAnnouncements();
			System.out.println("[MultiChat] The files were created!");

		}

		File f8 = new File(ConfigDir, "Bulletins.dat");

		if ((f8.exists()) && (!f8.isDirectory())) {

			loadBulletins();

		} else {

			System.out.println("[MultiChat] Some bulletins files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveBulletins();
			System.out.println("[MultiChat] The files were created!");

		}

		File f9 = new File(ConfigDir, "Casts.dat");

		if ((f9.exists()) && (!f9.isDirectory())) {

			CastControl.castList = loadCasts();

		} else {

			System.out.println("[MultiChat] Some casts files do not exist to load. Must be first startup!");
			System.out.println("[MultiChat] Welcome to MultiChat! :D");
			System.out.println("[MultiChat] Attempting to create hash files!");
			saveCasts();
			System.out.println("[MultiChat] The files were created!");

		}

		System.out.println("[MultiChat] [COMPLETE] Load sequence finished! (Any errors reported above)");

	}
}
