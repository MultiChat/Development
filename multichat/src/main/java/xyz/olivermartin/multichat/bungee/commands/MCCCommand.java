package xyz.olivermartin.multichat.bungee.commands;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.RegexUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

import java.util.UUID;

/**
 * Mod-Chat Colour Command
 * <p>Allows staff members to individually set the colours that they see the mod-chat displayed in</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MCCCommand extends Command {

    public MCCCommand() {
        super("mcmcc", "multichat.staff.mod", ProxyConfigs.ALIASES.getAliases("mcmcc"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "command_mcc_only_players");
            return;
        }

        if (args.length < 2) {
            MessageManager.sendMessage(sender, "command_mcc_usage");
            return;
        }

        String chatColor = args[0].toLowerCase();
        String nameColor = args[1].toLowerCase();

        if (!RegexUtil.LEGACY_COLOR.matches(chatColor) || !RegexUtil.LEGACY_COLOR.matches(nameColor)) {
            MessageManager.sendMessage(sender, "command_mcc_invalid");
            MessageManager.sendMessage(sender, "command_mcc_invalid_usage");
            return;
        }

        UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();
        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();
        TChatInfo chatInfo = proxyDataStore.getModChatPreferences().getOrDefault(playerUID, new TChatInfo());

        chatInfo.setChatColor(chatColor.charAt(0));
        chatInfo.setNameColor(nameColor.charAt(0));
        proxyDataStore.getModChatPreferences().put(playerUID, chatInfo);

        MessageManager.sendMessage(sender, "command_mcc_updated");
    }
}
