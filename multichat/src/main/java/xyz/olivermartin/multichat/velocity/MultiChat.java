package xyz.olivermartin.multichat.velocity;

import com.google.inject.Inject;
import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Getter;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import xyz.olivermartin.multichat.proxy.common.ServerGroups;
import xyz.olivermartin.multichat.velocity.commands.Command;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * The MAIN MultiChat Class
 * <p>This class is the main plugin. All plugin enable and disable control happens here.</p>
 *
 * @author Oliver Martin (Revilo410)
 */
@Plugin(id = "multichat", name = "MultiChat", version = "1.9.8", authors = {"Revilo410", "Haha007"})
public class MultiChat {

    public static final String LATEST_VERSION = "1.9.8";

    public static final String[] ALLOWED_VERSIONS = new String[]{
            LATEST_VERSION,
            "1.9.7",
            "1.9.6",
            "1.9.5",
            "1.9.4",
            "1.9.3",
            "1.9.2",
            "1.9.1",
            "1.9",
            "1.8.2",
            "1.8.1",
            "1.8",
            "1.7.5",
            "1.7.4",
            "1.7.3",
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

    public static Map<UUID, TChatInfo> modchatpreferences = new HashMap<>();
    public static Map<UUID, TChatInfo> adminchatpreferences = new HashMap<>();
    public static Map<String, TGroupChatInfo> groupchats = new HashMap<>();

    public static Map<UUID, String> viewedchats = new HashMap<>();
    public static Map<UUID, UUID> lastmsg = new HashMap<>();
    public static List<UUID> allspy = new ArrayList<>();
    public static List<UUID> socialspy = new ArrayList<>();

    public static File configDir;
    public static String configversion;

    public static boolean frozen;

    public static String defaultChannel = "";
    public static boolean forceChannelOnJoin = false;

    public static boolean logPMs = true;
    public static boolean logStaffChat = true;
    public static boolean logGroupChat = true;

    private static MultiChat instance;

    public static List<String> legacyServers = new ArrayList<>();

    private static ServerGroups serverGroups;
    private static Boolean serverGroupsEnabled = false;
    private static HashMap<String, ArrayList<String>> serverGroupsMap = new HashMap<>();

    @Getter
    private final Logger logger;
    @Getter
    private final ProxyServer server;
    private final Path dataFolderPath;

    public static MultiChat getInstance() {
        return instance;
    }

    @Inject
    public MultiChat(ProxyServer server, Logger logger, @DataDirectory Path dataFolderPath) {
        this.logger = logger;
        this.server = server;
        this.dataFolderPath = dataFolderPath;
    }

    @Subscribe
    void onProxyInit(ProxyInitializeEvent event) {
        onEnable();
    }

    @Subscribe
    void onProxyDisable(ProxyShutdownEvent event) {
        onDisable();
    }

    public void backup() {
        getServer().getScheduler().buildTask(this, () -> {
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
        }).delay(1, TimeUnit.MINUTES).repeat(60, TimeUnit.MINUTES).schedule();
    }

    public void fetchDisplayNames() {
        getServer().getScheduler().buildTask(this, () -> {
            if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                for (Player player : getServer().getAllPlayers()) {
                    if (player.getCurrentServer().isPresent()) {
                        BungeeComm.sendMessage(player.getGameProfile().getName(), player.getCurrentServer().get().getServerInfo());
                    }
                }
            }
        }).delay(1, TimeUnit.MINUTES).repeat(5, TimeUnit.MINUTES).schedule();
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        fetchDisplayNameOnce(event.getPlayer().getGameProfile().getName());
    }

    @Subscribe
    public void onServerSwitch(ServerPreConnectEvent event) {
        fetchDisplayNameOnce(event.getPlayer().getGameProfile().getName());
    }

    public void fetchDisplayNameOnce(final String playername) {
        getServer().getScheduler().buildTask(this, () -> {
            try {
                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                    getServer().getPlayer(playername).ifPresent(player -> {
                        if (player.getCurrentServer().isPresent()) {
                            BungeeComm.sendMessage(player.getGameProfile().getName(), player.getCurrentServer().get().getServerInfo());
                        }
                    });
                }
            } catch (NullPointerException ex) { /* EMPTY */ }
        }).schedule();

        getServer().getScheduler().buildTask(this, () -> {
            try {
                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                    getServer().getPlayer(playername).ifPresent(player -> {
                        if (player.getCurrentServer().isPresent()) {
                            BungeeComm.sendMessage(player.getGameProfile().getName(), player.getCurrentServer().get().getServerInfo());
                        }
                    });
                }
            } catch (NullPointerException ex) { /* EMPTY */ }
        }).delay(1L, TimeUnit.SECONDS).schedule();

        getServer().getScheduler().buildTask(this, () -> {
            try {
                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                    getServer().getPlayer(playername).ifPresent(player -> {
                        if (player.getCurrentServer().isPresent()) {
                            BungeeComm.sendMessage(player.getGameProfile().getName(), player.getCurrentServer().get().getServerInfo());
                        }
                    });
                }
            } catch (NullPointerException ex) { /* EMPTY */ }
        }).delay(2, TimeUnit.SECONDS).schedule();

        getServer().getScheduler().buildTask(this, () -> {
            try {
                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                    getServer().getPlayer(playername).ifPresent(player -> {
                        if (player.getCurrentServer().isPresent()) {
                            BungeeComm.sendMessage(player.getGameProfile().getName(), player.getCurrentServer().get().getServerInfo());
                        }
                    });
                }
            } catch (NullPointerException ex) { /* EMPTY */ }
        }).delay(4L, TimeUnit.SECONDS).schedule();
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
        ConfigManager.getInstance().registerHandler("config_fr.yml", new File(translationsDir));
        ConfigManager.getInstance().registerHandler("chatcontrol_fr.yml", new File(translationsDir));

        ConfigurationNode configYML = ConfigManager.getInstance().getHandler("config.yml").getConfig();
        ConfigurationNode chatcontrolYML = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        configversion = configYML.getNode("version").getString();

        if (Arrays.asList(ALLOWED_VERSIONS).contains(configversion)) {

            // TODO - Remove for future versions!
            if (!Objects.equals(configversion, LATEST_VERSION)) {

                getLogger().info("[!!!] [WARNING] YOUR CONFIG FILES ARE NOT THE LATEST VERSION");
                getLogger().info("[!!!] [WARNING] MULTICHAT HAS INTRODUCES SEVERAL NEW FEATURES WHICH ARE NOT IN YOUR OLD FILE");
                getLogger().info("[!!!] [WARNING] THE PLUGIN SHOULD WORK WITH THE OLDER FILE, BUT IS NOT SUPPORTED!");
                getLogger().info("[!!!] [WARNING] PLEASE BACKUP YOUR OLD CONFIG FILES AND DELETE THEM FROM THE MULTICHAT FOLDER SO NEW ONES CAN BE GENERATED!");
                getLogger().info("[!!!] [WARNING] THANK YOU");

            }

            // Register listeners
            getServer().getEventManager().register(this, new Events());

            // Register communication channels and appropriate listeners
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:comm"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:prefix"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:suffix"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:dn"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:nick"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:world"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:act"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:pact"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:chat"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:ch"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:ignore"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:pxe"));
            getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from("multichat:ppxe"));
            getServer().getEventManager().register(this, new BungeeComm());

            // Register commands
            registerCommands(configYML, chatcontrolYML);

            System.out.println("[MultiChat] Config Version: " + configversion);

            // Run start-up routines
            Startup();
            UUIDNameManager.Startup();

            // Set up chat control stuff
            if (chatcontrolYML.getChildrenMap().containsKey("link_control")) {
                ChatControl.controlLinks = chatcontrolYML.getNode("link_control").getBoolean();
                ChatControl.linkMessage = chatcontrolYML.getNode("link_removal_message").getString();
                if (chatcontrolYML.getChildrenMap().containsKey("link_regex")) {
                    ChatControl.linkRegex = chatcontrolYML.getNode("link_regex").getString();
                }
            }

            if (configYML.getChildrenMap().containsKey("privacy_settings")) {
                logPMs = configYML.getNode("privacy_settings").getNode("log_pms").getBoolean();
                logStaffChat = configYML.getNode("privacy_settings").getNode("log_staffchat").getBoolean();
                logGroupChat = configYML.getNode("privacy_settings").getNode("log_groupchat").getBoolean();
            }

            // Legacy servers for RGB approximation
            if (configYML.getChildrenMap().containsKey("legacy_servers")) {
                legacyServers = configYML.getNode("legacy_servers").getList(o -> (String) o);
            }

            // Set default channel
            defaultChannel = configYML.getNode("default_channel").getString();
            forceChannelOnJoin = configYML.getNode("force_channel_on_join").getBoolean();

            // Set up global chat
            GlobalChannel channel = Channel.getGlobalChannel();
            channel.setFormat(configYML.getNode("globalformat").getString());

            // Add all appropriate servers to this hardcoded global chat stream
            for (String server : configYML.getNode("no_global").getList(o -> (String) o)) {
                channel.addServer(server);
            }

            // Setup Server Groups for Global Chatting
            serverGroups = new ServerGroups();

            if (configYML.getNode("serverGroups") != null) {
                serverGroupsEnabled = configYML.getNode("serverGroups").getNode("enabled").getBoolean(false);

                if (configYML.getNode("serverGroups").getNode("groups") != null && configYML.getNode("serverGroups").getNode("groups").getChildrenMap() != null) {
                    for (Map.Entry<Object, ? extends ConfigurationNode> groupKey : configYML.getNode("serverGroups").getNode("groups").getChildrenMap().entrySet()) {
                        ArrayList<String> serverGroupList = new ArrayList<>();

                        for (String server : configYML.getNode("serverGroups").getNode("groups").getNode(groupKey.getKey()).getList(o -> (String) o)) {
                            serverGroupList.add(server);
                        }

                        serverGroupsMap.put(groupKey.getKey().toString(), serverGroupList);
                    }
                }
            } else {
                serverGroupsEnabled = false;
            }

            serverGroups.setServerGroupsEnabled(serverGroupsEnabled);
            serverGroups.setServerGroups(serverGroupsMap);

            // Initiate backup routine
            backup();

            // Fetch display names of all players
            fetchDisplayNames();
        } else {
            getLogger().info("Config incorrect version! Please repair or delete it!");
        }
    }

    private File getDataFolder() {
        return dataFolderPath.toFile();
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

    private void registerCommand(Command command) {
        getServer().getCommandManager().register(command.getMeta(), command);
    }

    public void registerCommands(ConfigurationNode configYML, ConfigurationNode chatcontrolYML) {

        // Register main commands
        registerCommand(CommandManager.getAcc());
        registerCommand(CommandManager.getAc());
        registerCommand(CommandManager.getMcc());
        registerCommand(CommandManager.getMc());
        registerCommand(CommandManager.getGc());
        registerCommand(CommandManager.getGroup());
        registerCommand(CommandManager.getGrouplist());
        registerCommand(CommandManager.getMultichat());
        registerCommand(CommandManager.getMultichatBypass());
        registerCommand(CommandManager.getMultiChatExecute());
        registerCommand(CommandManager.getDisplay());
        registerCommand(CommandManager.getFreezechat());
        registerCommand(CommandManager.getHelpme());
        registerCommand(CommandManager.getClearchat());
        registerCommand(CommandManager.getAnnouncement());
        registerCommand(CommandManager.getBulletin());
        registerCommand(CommandManager.getCast());
        registerCommand(CommandManager.getUsecast());
        registerCommand(CommandManager.getIgnore());

        // Register PM commands
        if (configYML.getNode("pm").getBoolean()) {
            registerCommand(CommandManager.getMsg());
            registerCommand(CommandManager.getReply());
            registerCommand(CommandManager.getSocialspy());
        }

        // Register global chat commands
        if (configYML.getNode("global").getBoolean()) {
            registerCommand(CommandManager.getLocal());
            registerCommand(CommandManager.getGlobal());
            registerCommand(CommandManager.getChannel());
        }

        // Register staff list command /staff
        if (configYML.getChildrenMap().containsKey("staff_list")) {
            if (configYML.getNode("staff_list").getBoolean()) {
                registerCommand(CommandManager.getStafflist());
            }
        } else {
            registerCommand(CommandManager.getStafflist());
        }

        // Register mute command
        if (chatcontrolYML.getNode("mute").getBoolean()) {
            registerCommand(CommandManager.getMute());
        }

    }

    public void unregisterCommands(ConfigurationNode configYML, ConfigurationNode chatcontrolYML) {

        // Unregister main commands
        unregisterCommand(CommandManager.getAcc());
        unregisterCommand(CommandManager.getAc());
        unregisterCommand(CommandManager.getMcc());
        unregisterCommand(CommandManager.getMc());
        unregisterCommand(CommandManager.getGc());
        unregisterCommand(CommandManager.getGroup());
        unregisterCommand(CommandManager.getGrouplist());
        unregisterCommand(CommandManager.getMultichat());
        unregisterCommand(CommandManager.getMultichatBypass());
        unregisterCommand(CommandManager.getMultiChatExecute());
        unregisterCommand(CommandManager.getDisplay());
        unregisterCommand(CommandManager.getFreezechat());
        unregisterCommand(CommandManager.getHelpme());
        unregisterCommand(CommandManager.getClearchat());
        unregisterCommand(CommandManager.getAnnouncement());
        unregisterCommand(CommandManager.getBulletin());
        unregisterCommand(CommandManager.getCast());
        unregisterCommand(CommandManager.getUsecast());
        unregisterCommand(CommandManager.getIgnore());

        // Unregister PM commands
        if (configYML.getNode("pm").getBoolean()) {
            unregisterCommand(CommandManager.getMsg());
            unregisterCommand(CommandManager.getReply());
            unregisterCommand(CommandManager.getSocialspy());
        }

        // Unregister global chat commands
        if (configYML.getNode("global").getBoolean()) {
            unregisterCommand(CommandManager.getLocal());
            unregisterCommand(CommandManager.getGlobal());
            unregisterCommand(CommandManager.getChannel());
        }

        // Unregister staff list command /staff
        if (configYML.getChildrenMap().containsKey("staff_list")) {
            if (configYML.getNode("staff_list").getBoolean()) {
                unregisterCommand(CommandManager.getStafflist());
            }
        } else {
            unregisterCommand(CommandManager.getStafflist());
        }

        // UnRegister mute command
        if (chatcontrolYML.getNode("mute").getBoolean()) {
            unregisterCommand(CommandManager.getMute());
        }

    }

    private void unregisterCommand(Command acc) {
        var cm = getServer().getCommandManager();
        acc.getMeta().getAliases().forEach(cm::unregister);
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

        try {
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
        } catch (IOException e) {
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

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (config.getNode("session_ignore").getBoolean()) return;

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
            result = (HashMap<UUID, TChatInfo>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[MultiChat] [Load Error] An error has occured reading the mod chat info file!");
            e.printStackTrace();
        }

        return result;

    }

    @SuppressWarnings("unchecked")
    public static void loadBulletins() {

        ArrayList<String> result;
        boolean enabled;
        int timeBetween;

        try {
            File file = new File(configDir, "Bulletins.dat");
            FileInputStream saveFile = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(saveFile);
            enabled = in.readBoolean();
            timeBetween = in.readInt();
            result = (ArrayList<String>) in.readObject();
            in.close();
            Bulletins.setArrayList(result);
            if (enabled) {
                Bulletins.startBulletins(timeBetween);
            }
        } catch (IOException | ClassNotFoundException e) {
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
            result = (HashMap<String, String>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
            result = (HashMap<UUID, TChatInfo>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[MultiChat] [Load Error] An error has occured reading the admin chat info file!");
            e.printStackTrace();
        }

        return result;

    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> loadCasts() {

        HashMap<String, String> result = null;

        try {
            File file = new File(configDir, "Casts.dat");
            FileInputStream saveFile = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(saveFile);
            result = (HashMap<String, String>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
            result = (HashMap<String, TGroupChatInfo>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
            result = (List<UUID>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
            result = (List<UUID>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
            result = (Map<UUID, Boolean>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
            result = (Set<UUID>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[MultiChat] [Load Error] An error has occured reading the mute file!");
            e.printStackTrace();
        }

        return result;

    }

    @SuppressWarnings("unchecked")
    public static Map<UUID, Set<UUID>> loadIgnore() {

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (config.getNode("session_ignore").getBoolean()) return new HashMap<>();

        Map<UUID, Set<UUID>> result = null;

        try {
            File file = new File(configDir, "Ignore.dat");
            FileInputStream saveFile = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(saveFile);
            result = (Map<UUID, Set<UUID>>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
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
