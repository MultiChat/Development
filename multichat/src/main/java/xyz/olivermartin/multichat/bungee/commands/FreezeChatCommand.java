package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Freeze Chat Command
 * <p>Allows staff members to block all chat messages being sent</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class FreezeChatCommand extends Command {

    public FreezeChatCommand() {
        super("mcfreezechat", "multichat.chat.freeze", ProxyConfigs.ALIASES.getAliases("mcfreezechat"));
    }

    public void execute(CommandSender sender, String[] args) {
        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();
        boolean frozen = !proxyDataStore.isChatFrozen();
        proxyDataStore.setChatFrozen(frozen);
        ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer ->
                MessageManager.sendSpecialMessage(proxiedPlayer,
                        "command_freezechat_" + (frozen ? "frozen" : "thawed"),
                        proxiedPlayer.getName()
                )
        );
    }
}
