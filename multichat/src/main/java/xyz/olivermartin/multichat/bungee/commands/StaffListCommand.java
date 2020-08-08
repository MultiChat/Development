package xyz.olivermartin.multichat.bungee.commands;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Staff List Command
 * <p>Allows the user to view a list of all online staff, sorted by their server</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class StaffListCommand extends Command {

    public StaffListCommand() {
        super("mcstaff", "multichat.staff.list", ProxyConfigs.ALIASES.getAliases("mcstaff"));
    }

    public void execute(CommandSender sender, String[] args) {
        MessageManager.sendMessage(sender, "command_stafflist_list");

        AtomicBoolean anyStaff = new AtomicBoolean(false);

        ProxyServer.getInstance().getServers().values().stream()
                .filter(serverInfo -> serverInfo.getPlayers().size() > 0)
                .forEach(serverInfo -> {
                    MessageManager.sendSpecialMessage(sender, "command_stafflist_list_server", serverInfo.getName());

                    serverInfo.getPlayers().stream()
                            .filter(target -> target.hasPermission("multichat.staff")
                                    && !(MultiChat.premiumVanish
                                    && ProxyConfigs.CONFIG.isPvPreventStaffList()
                                    && BungeeVanishAPI.isInvisible(target)
                                    && !sender.hasPermission("multichat.staff.list.vanished"))
                            )
                            .forEach(target -> {
                                if (!anyStaff.get())
                                    anyStaff.set(true);

                                if (ProxyConfigs.CONFIG.isFetchSpigotDisplayNames())
                                    ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(target.getName(), serverInfo);

                                MessageManager.sendSpecialMessage(sender, "command_stafflist_list_item", target.getDisplayName());
                            });

                    // TODO: We should decide when or how "no staff is online" is shown
                    //  If we want to keep it like this, we should replace the stream with a normal for
                    if (!anyStaff.get())
                        MessageManager.sendMessage(sender, "command_stafflist_no_staff");
                    else
                        anyStaff.set(false);
                });


    }
}
