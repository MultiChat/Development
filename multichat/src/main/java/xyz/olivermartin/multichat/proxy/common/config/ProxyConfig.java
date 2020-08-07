package xyz.olivermartin.multichat.proxy.common.config;

import xyz.olivermartin.multichat.common.RegexUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to represent the proxy's config.yml.
 * <p>
 * All methods should be relatively straight forward and represent their respective entries in the config.
 */
public class ProxyConfig extends AbstractProxyConfig {

    private String version, displayNameFormat, pmOutFormat, pmInFormat, pmSpyFormat, defaultChannel, globalFormat,
            localSpyFormat, groupChatFormat, groupChatColor, groupNameColor, modChatFormat, modChatColor, modNameColor,
            adminChatFormat, adminChatColor, adminNameColor;
    private boolean fetchSpigotDisplayNames, setDisplayName, pm, togglePm, forceChannelOnJoin, global, staffList,
            logPms, logStaffChat, logGroupChat, pvPreventMessage, pvPreventStaffList, pvSilenceJoin;
    private final Set<String> noPmServers = new HashSet<>(), localServers = new HashSet<>(), legacyServers = new HashSet<>();

    ProxyConfig() {
        super("config.yml");
    }

    @Override
    void reloadValues() {
        noPmServers.clear();
        localServers.clear();
        legacyServers.clear();

        version = getConfig().getString("version", "1.10");

        fetchSpigotDisplayNames = getConfig().getBoolean("fetch_spigot_display_names", true);
        setDisplayName = getConfig().getBoolean("set_display_name", true);
        displayNameFormat = getConfig().getString("display_name_format", "%PREFIX%%NICK%%SUFFIX%");

        pm = getConfig().getBoolean("pm", true);
        noPmServers.addAll(getConfig().getStringList("no_pm"));
        togglePm = getConfig().getBoolean("toggle_pm", true);

        pmOutFormat = getConfig().getString("pmout", "&6[&cYou &6-> &c%DISPLAYNAMET%&6] &f%MESSAGE%");
        pmInFormat = getConfig().getString("pmin", "&6[&c%DISPLAYNAME% &6-> &cMe&6] &f%MESSAGE%");
        pmSpyFormat = getConfig().getString("pmSpyFormat", "&8&l<< &f%NAME% &7-> &f%NAMET%&8: &7%MESSAGE% &8&l>>");

        defaultChannel = getConfig().getString("default_channel", "global");
        if (!defaultChannel.equals("global") && !defaultChannel.equals("local"))
            defaultChannel = "global";
        forceChannelOnJoin = getConfig().getBoolean("force_channel_on_join", forceChannelOnJoin);

        global = getConfig().getBoolean("global", true);
        localServers.addAll(getConfig().getStringList("no_global"));

        globalFormat = getConfig().getString("globalformat", "&2[&aG&2] &f%DISPLAYNAME%&f: ");
        localSpyFormat = getConfig().getString("localspyformat", "&8[&7SPY&8] %FORMAT%");

        groupChatFormat = getConfig().getString("groupchat.format", "%CC%(%NC%%GROUPNAME%%CC%)(%NC%%NAME%%CC%) %MESSAGE%");
        groupChatColor = getConfig().getString("groupchat.ccdefault", "a");
        if (!RegexUtil.LEGACY_COLOR.matches(groupChatColor))
            groupChatColor = "a";
        groupNameColor = getConfig().getString("groupchat.ncdefault", "f");
        if (!RegexUtil.LEGACY_COLOR.matches(groupNameColor))
            groupNameColor = "f";

        modChatFormat = getConfig().getString("modchat.format", "%CC%{%NC%%NAME%%CC%} %MESSAGE%");
        modChatColor = getConfig().getString("modchat.ccdefault", "b");
        if (!RegexUtil.LEGACY_COLOR.matches(modChatColor))
            modChatColor = "b";
        modNameColor = getConfig().getString("modchat.ncdefault", "d");
        if (!RegexUtil.LEGACY_COLOR.matches(modNameColor))
            modNameColor = "d";

        adminChatFormat = getConfig().getString("adminchat.format", "%CC%{%NC%%NAME%%CC%} %MESSAGE%");
        adminChatColor = getConfig().getString("adminchat.ccdefault", "d");
        if (!RegexUtil.LEGACY_COLOR.matches(adminChatColor))
            adminChatColor = "d";
        adminNameColor = getConfig().getString("adminchat.ncdefault", "b");
        if (!RegexUtil.LEGACY_COLOR.matches(adminNameColor))
            adminNameColor = "b";

        staffList = getConfig().getBoolean("staff_list", true);

        logPms = getConfig().getBoolean("privacy_settings.log_pms", true);
        logStaffChat = getConfig().getBoolean("privacy_settings.log_staffchat", true);
        logGroupChat = getConfig().getBoolean("privacy_settings.log_groupochat", true);

        pvPreventMessage = getConfig().getBoolean("premium_vanish.prevent_message", true);
        pvPreventStaffList = getConfig().getBoolean("premium_vanish.prevent_staff_list", true);
        pvSilenceJoin = getConfig().getBoolean("premium_vanish.silence_join", true);

        legacyServers.addAll(getConfig().getStringList("legacy_servers"));
    }

    public String getVersion() {
        return version;
    }

    public boolean isFetchSpigotDisplayNames() {
        return fetchSpigotDisplayNames;
    }

    public boolean isSetDisplayName() {
        return setDisplayName;
    }

    public String getDisplayNameFormat() {
        return displayNameFormat;
    }

    public boolean isPm() {
        return pm;
    }

    public boolean isNoPmServer(String serverName) {
        return noPmServers.contains(serverName);
    }

    public boolean isTogglePm() {
        return togglePm;
    }

    public String getPmOutFormat() {
        return pmOutFormat;
    }

    public String getPmInFormat() {
        return pmInFormat;
    }

    public String getPmSpyFormat() {
        return pmSpyFormat;
    }

    public String getDefaultChannel() {
        return defaultChannel;
    }

    public boolean isForceChannelOnJoin() {
        return forceChannelOnJoin;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isGlobalServer(String serverName) {
        return !localServers.contains(serverName);
    }

    public String getGlobalFormat() {
        return globalFormat;
    }

    public String getLocalSpyFormat() {
        return localSpyFormat;
    }

    public String getGroupChatFormat() {
        return groupChatFormat;
    }

    public String getGroupChatColor() {
        return groupChatColor;
    }

    public String getGroupNameColor() {
        return groupNameColor;
    }

    public String getModChatFormat() {
        return modChatFormat;
    }

    public String getModChatColor() {
        return modChatColor;
    }

    public String getModNameColor() {
        return modNameColor;
    }

    public String getAdminChatFormat() {
        return adminChatFormat;
    }

    public String getAdminChatColor() {
        return adminChatColor;
    }

    public String getAdminNameColor() {
        return adminNameColor;
    }

    public boolean isStaffList() {
        return staffList;
    }

    public boolean isLogPms() {
        return logPms;
    }

    public boolean isLogStaffChat() {
        return logStaffChat;
    }

    public boolean isLogGroupChat() {
        return logGroupChat;
    }

    public boolean isPvPreventMessage() {
        return pvPreventMessage;
    }

    public boolean isPvPreventStaffList() {
        return pvPreventStaffList;
    }

    public boolean isPvSilenceJoin() {
        return pvSilenceJoin;
    }

    public boolean isLegacyServer(String serverName) {
        return legacyServers.contains(serverName);
    }
}
