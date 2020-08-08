package xyz.olivermartin.multichat.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.proxy.common.*;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.TagManager;
import xyz.olivermartin.multichat.proxy.common.channels.local.LocalChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.GlobalStaticProxyChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.ProxyChannelInfo;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.StaticProxyChannel;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.contexts.ContextManager;
import xyz.olivermartin.multichat.proxy.common.contexts.GlobalContext;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyLoginListener;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyLogoutListener;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyServerConnectedListener;
import xyz.olivermartin.multichat.proxy.common.listeners.ProxyServerSwitchListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyPlayerActionListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyPlayerChatListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyPlayerMetaListener;
import xyz.olivermartin.multichat.proxy.common.listeners.communication.ProxyServerActionListener;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyFileStoreManager;
import xyz.olivermartin.multichat.proxy.common.storage.files.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * The MAIN MultiChat Class
 * <p>This class is the main plugin. All plugin enable and disable control happens here.</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MultiChat extends Plugin {

    public static final String LATEST_VERSION = "1.10";

    public static final String[] ALLOWED_VERSIONS = new String[]{

            LATEST_VERSION,
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

    // Config values
    public static boolean premiumVanish = false;

    public void fetchDisplayNames() {

        getProxy().getScheduler().schedule(this, new Runnable() {

            public void run() {

                if (ProxyConfigs.CONFIG.isFetchSpigotDisplayNames()) {

                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if (player.getServer() != null) {
                            ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
                        }
                    }

                }

            }

        }, 1L, 5L, TimeUnit.MINUTES);

    }

    public void onEnable() {

        MultiChatProxy.getInstance().registerPlugin(this);

        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this);

        MultiChatProxyPlatform platform = MultiChatProxyPlatform.BUNGEE;
        MultiChatProxy.getInstance().registerPlatform(platform);

        ProxyDataStore dataStore = new ProxyDataStore();
        MultiChatProxy.getInstance().registerDataStore(dataStore);

        File configDirectory = getDataFolder();
        if (!getDataFolder().exists()) {
            System.out.println("[MultiChat] Creating plugin directory!");
            getDataFolder().mkdirs();
        }
        MultiChatProxy.getInstance().registerConfigDirectory(configDirectory);

        File translationsDir = new File(configDirectory.toString() + File.separator + "translations");
        if (!translationsDir.exists()) {
            System.out.println("[MultiChat] Creating translations directory!");
            translationsDir.mkdirs();
        }

        ProxyConfigs.ALL.forEach(abstractProxyConfig -> abstractProxyConfig.reloadConfig(this));

        // TODO: [ConfigRefactor] Maybe have a list somewhere through which we can simply loop and call #loadRawConfig
        //  for future translations
        ProxyConfigs.loadRawConfig(this, "messages_fr.yml", translationsDir);
        ProxyConfigs.loadRawConfig(this, "joinmessages_fr.yml", translationsDir);
        ProxyConfigs.loadRawConfig(this, "config_fr.yml", translationsDir);
        ProxyConfigs.loadRawConfig(this, "chatcontrol_fr.yml", translationsDir);
        ProxyConfigs.loadRawConfig(this, "aliases_fr.yml", translationsDir);

        if (Arrays.asList(ALLOWED_VERSIONS).contains(ProxyConfigs.CONFIG.getVersion())) {

            if (!ProxyConfigs.CONFIG.getVersion().equals(LATEST_VERSION)) {
                getLogger().warning("YOUR CONFIG FILES ARE NOT THE LATEST VERSION");
                getLogger().warning("SOME FEATURES OF MULTICHAT ARE ONLY PRESENT IN THE LATEST VERSION OF THE CONFIG");
            }

            // Register listeners
            getProxy().getPluginManager().registerListener(this, new Events());

            // New listeners (1.10+)
            getProxy().getPluginManager().registerListener(this, new ProxyLoginListener());
            getProxy().getPluginManager().registerListener(this, new ProxyServerConnectedListener());
            getProxy().getPluginManager().registerListener(this, new ProxyLogoutListener());
            getProxy().getPluginManager().registerListener(this, new ProxyServerSwitchListener());

            // Communication Channels
            getProxy().registerChannel(CommChannels.PLAYER_META); // pmeta
            getProxy().registerChannel(CommChannels.PLAYER_CHAT); // pchat
            getProxy().registerChannel(CommChannels.SERVER_CHAT); // schat
            getProxy().registerChannel(CommChannels.PLAYER_ACTION); // pact
            getProxy().registerChannel(CommChannels.SERVER_ACTION); // sact
            getProxy().registerChannel(CommChannels.PLAYER_DATA); // pdata
            getProxy().registerChannel(CommChannels.SERVER_DATA); // sdata
            getProxy().getPluginManager().registerListener(this, new ProxyPlayerMetaListener()); // list - pmeta
            getProxy().getPluginManager().registerListener(this, new ProxyPlayerChatListener()); // list - pchat
            getProxy().getPluginManager().registerListener(this, new ProxyPlayerActionListener()); // list - pact
            getProxy().getPluginManager().registerListener(this, new ProxyServerActionListener()); // list - sact

            // Register commands
            registerCommands();

            // TODO: Change to appropriate logger
            System.out.println("[MultiChat] Config Version: " + ProxyConfigs.CONFIG.getVersion());

            // Run start-up routines
            ProxyFileStoreManager fileStoreManager = new ProxyFileStoreManager();

            fileStoreManager.registerFileStore("announcements.dat",
                    new ProxyAnnouncementsFileStore("Announcements.dat", configDirectory));

            fileStoreManager.registerFileStore("bulletins.dat",
                    new ProxyBulletinsFileStore("Bulletins.dat", configDirectory));

            fileStoreManager.registerFileStore("staffchatinfo.dat",
                    new ProxyStaffChatFileStore("StaffChatInfo.dat", configDirectory));

            fileStoreManager.registerFileStore("adminchatinfo.dat",
                    new ProxyAdminChatFileStore("AdminChatInfo.dat", configDirectory));

            fileStoreManager.registerFileStore("groupchatinfo.dat",
                    new ProxyGroupChatFileStore("GroupChatInfo.dat", configDirectory));

            fileStoreManager.registerFileStore("groupspyinfo.dat",
                    new ProxyGroupSpyFileStore("GroupSpyInfo.dat", configDirectory));

            fileStoreManager.registerFileStore("casts.dat",
                    new ProxyCastsFileStore("Casts.dat", configDirectory));

            fileStoreManager.registerFileStore("socialspyinfo.dat",
                    new ProxySocialSpyFileStore("SocialSpyInfo.dat", configDirectory));

            fileStoreManager.registerFileStore("globalchatinfo.dat",
                    new ProxyGlobalChatFileStore("GlobalChatInfo.dat", configDirectory));

            fileStoreManager.registerFileStore("mute.dat",
                    new ProxyMuteFileStore("Mute.dat", configDirectory));

            fileStoreManager.registerFileStore("ignore.dat",
                    new ProxyIgnoreFileStore("Ignore.dat", configDirectory));

            fileStoreManager.registerFileStore("multichatuuidname.dat",
                    new ProxyUUIDNameFileStore("MultiChatUUIDName.dat", configDirectory));

            fileStoreManager.registerFileStore("localspyinfo.dat",
                    new ProxyLocalSpyFileStore("LocalSpyInfo.dat", configDirectory));

            MultiChatProxy.getInstance().registerFileStoreManager(fileStoreManager);

            // Set default channel
            String defaultChannel = ProxyConfigs.CONFIG.getDefaultChannel();
            boolean forceChannelOnJoin = ProxyConfigs.CONFIG.isForceChannelOnJoin();

            // New context manager and channels
            GlobalContext globalContext = new GlobalContext(defaultChannel, forceChannelOnJoin, true);
            ContextManager contextManager = new ContextManager(globalContext);
            MultiChatProxy.getInstance().registerContextManager(contextManager);

            ChannelManager channelManager = new ChannelManager();
            channelManager.setGlobalChannel(new GlobalStaticProxyChannel("Global Channel",
                    ProxyConfigs.CONFIG.getGlobalFormat(),
                    Arrays.asList(ProxyConfigs.ALIASES.getAliases("global")),
                    channelManager)
            );
            channelManager.setLocalChannel(new LocalChannel("Local Channel",
                    ProxyConfigs.CONFIG.getGlobalFormat(),
                    Arrays.asList(ProxyConfigs.ALIASES.getAliases("local")),
                    channelManager)
            );
            MultiChatProxy.getInstance().registerChannelManager(channelManager);

            // TODO This is just a test channel
            channelManager.registerProxyChannel(new StaticProxyChannel("test", new ProxyChannelInfo("A test channel", "&8[&7TEST&8] &f%DISPLAYNAME%&f: ", false, globalContext, new ArrayList<String>(), "multichat.chat.channel.test", "multichat.chat.channel.test.view"), channelManager));

            ProxyChatManager chatManager = new ProxyChatManager();
            MultiChatProxy.getInstance().registerChatManager(chatManager);

            MultiChatProxy.getInstance().registerTagManager(new TagManager());

            ///

            // Initiate backup routine
            ProxyBackupManager backupManager = new ProxyBackupManager();
            MultiChatProxy.getInstance().registerBackupManager(backupManager);
            backupManager.registerBackupTask(new Runnable() {

                public void run() {
                    MultiChatProxy.getInstance().getFileStoreManager().save();
                }

            });
            backupManager.startBackup(1L, 60L, TimeUnit.MINUTES);

            // Fetch display names of all players
            fetchDisplayNames();

            // Manage premiumVanish dependency
            if (ProxyServer.getInstance().getPluginManager().getPlugin("PremiumVanish") != null) {
                premiumVanish = true;
                System.out.println("[MultiChat] Hooked with PremiumVanish!");
            }

        } else {
            getLogger().info("Config incorrect version! Please repair or delete it!");
        }
    }

    public void onDisable() {

        getLogger().info("Thankyou for using MultiChat. Disabling...");

        MultiChatProxy.getInstance().getFileStoreManager().save();

    }

    public void registerCommands() {

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
        if (ProxyConfigs.CONFIG.isPm()) {
            getProxy().getPluginManager().registerCommand(this, CommandManager.getMsg());
            getProxy().getPluginManager().registerCommand(this, CommandManager.getReply());
            getProxy().getPluginManager().registerCommand(this, CommandManager.getSocialspy());
        }

        // Register global chat commands
        if (ProxyConfigs.CONFIG.isGlobal()) {
            getProxy().getPluginManager().registerCommand(this, CommandManager.getLocal());
            getProxy().getPluginManager().registerCommand(this, CommandManager.getGlobal());
            getProxy().getPluginManager().registerCommand(this, CommandManager.getChannel());
            getProxy().getPluginManager().registerCommand(this, CommandManager.getLocalspy());
        }

        // Register staff list command /staff
        if (ProxyConfigs.CONFIG.isStaffList()) {
            getProxy().getPluginManager().registerCommand(this, CommandManager.getStafflist());
        }

        // Register mute command
        if (ProxyConfigs.CHAT_CONTROL.isMute()) {
            getProxy().getPluginManager().registerCommand(this, CommandManager.getMute());
        }

    }

    public void unregisterCommands() {
        getProxy().getPluginManager().unregisterCommands(this);
    }

}
