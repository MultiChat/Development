package xyz.olivermartin.multichat.proxy.common;

import com.velocitypowered.api.proxy.Player;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerGroups {
    private static Boolean serverGroupsEnabled = false;
    private static HashMap<String, ArrayList<String>> serverGroups;

    public ServerGroups() {

    }

    public static Boolean getServerGroupsEnabled() {
        return serverGroupsEnabled;
    }

    public void setServerGroupsEnabled(Boolean serverGroupsEnabled) {
        this.serverGroupsEnabled = serverGroupsEnabled;
    }

    public HashMap<String, ArrayList<String>> getServerGroups() {
        return serverGroups;
    }

    public void setServerGroups(HashMap<String, ArrayList<String>> serverGroups) {
        this.serverGroups = serverGroups;
    }

    public static ArrayList<String> getServerGroupList(ProxiedPlayer sender) {
        if (serverGroupsEnabled && serverGroups != null && sender != null && sender.getServer() != null) {
            String serverName = sender.getServer().getInfo().getName();

            for (HashMap.Entry<String, ArrayList<String>> serverGroup : serverGroups.entrySet()) {
                if (serverGroup.getValue().contains(serverName)) {
                    return serverGroup.getValue();
                }
            }
        }

        return null;
    }

    public static ArrayList<String> getServerGroupList(Player sender) {
        if (serverGroupsEnabled && serverGroups != null && sender != null && sender.getCurrentServer() != null) {
            String serverName = sender.getCurrentServer().get().getServerInfo().getName();

            for (HashMap.Entry<String, ArrayList<String>> serverGroup : serverGroups.entrySet()) {
                if (serverGroup.getValue().contains(serverName)) {
                    return serverGroup.getValue();
                }
            }
        }

        return null;
    }

    public static String getServerGroupName(Player sender) {
        if (serverGroupsEnabled && serverGroups != null && sender != null && sender.getCurrentServer() != null) {
            String serverName = sender.getCurrentServer().get().getServerInfo().getName();

            for (HashMap.Entry<String, ArrayList<String>> serverGroup : serverGroups.entrySet()) {
                if (serverGroup.getValue().contains(serverName)) {
                    return serverGroup.getKey();
                }
            }
        }

        return null;
    }
}
