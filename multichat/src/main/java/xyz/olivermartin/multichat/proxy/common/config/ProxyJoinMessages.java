package xyz.olivermartin.multichat.proxy.common.config;

public class ProxyJoinMessages extends AbstractProxyConfig {

    private String version, serverJoin, silentJoin, networkQuit, silentQuit, welcomeMessage, privateWelcomeMessage;
    private boolean showJoin, showQuit, welcome, privateWelcome;

    ProxyJoinMessages() {
        super("joinmessages.yml");
    }

    @Override
    void reloadValues() {
        version = getConfig().getString("version", "1.10");

        showJoin = getConfig().getBoolean("showjoin", true);
        showQuit = getConfig().getBoolean("showquit", true);

        serverJoin = getConfig().getString("serverjoin", "&e%NAME% &ejoined the network");
        silentJoin = getConfig().getString("silentjoin", "&b&o%NAME% &b&ojoined the network silently");
        networkQuit = getConfig().getString("networkquit", "&e%NAME% left the network");
        silentQuit = getConfig().getString("silentquit", "&b&o%NAME% &b&oleft the network silently");

        welcome = getConfig().getBoolean("welcome", true);
        welcomeMessage = getConfig().getString("welcome_message", "&dWelcome %NAME% to the network for the first time!");
        privateWelcome = getConfig().getBoolean("private_welcome", false);
        privateWelcomeMessage = getConfig().getString("private_welcome_message", "&5Hi there %NAME%, please make sure you read the /rules!");
    }

    public String getVersion() {
        return version;
    }

    public boolean isShowJoin() {
        return showJoin;
    }

    public boolean isShowQuit() {
        return showQuit;
    }

    public String getServerJoin() {
        return serverJoin;
    }

    public String getSilentJoin() {
        return silentJoin;
    }

    public String getNetworkQuit() {
        return networkQuit;
    }

    public String getSilentQuit() {
        return silentQuit;
    }

    public boolean isWelcome() {
        return welcome;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public boolean isPrivateWelcome() {
        return privateWelcome;
    }

    public String getPrivateWelcomeMessage() {
        return privateWelcomeMessage;
    }
}
