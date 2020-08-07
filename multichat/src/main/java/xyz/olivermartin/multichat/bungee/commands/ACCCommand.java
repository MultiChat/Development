package xyz.olivermartin.multichat.bungee.commands;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.RegexUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Admin-Chat colour command
 * <p> This command allows individual staff members to set their colour of the admin-chat messages they receive</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ACCCommand extends Command {

	public ACCCommand() {
		super("mcacc", "multichat.staff.admin", ProxyConfigs.ALIASES.getAliases("mcacc"));
	}

	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			MessageManager.sendMessage(sender, "command_acc_only_players");
			return;
		}
		DebugManager.log("[ACCCommand] Command sender is a player");

		if (args.length != 2) {
			MessageManager.sendMessage(sender, "command_acc_usage");
			return;
		}
		args[0] = args[0].toLowerCase();
		args[1] = args[1].toLowerCase();

		if (!RegexUtil.LEGACY_COLOR.matches(args[0]) || !RegexUtil.LEGACY_COLOR.matches(args[1])) {
			MessageManager.sendMessage(sender, "command_acc_invalid");
			MessageManager.sendMessage(sender, "command_acc_invalid_usage");
			return;
		}

		DebugManager.log("[ACCCommand] Colour codes are valid");

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		TChatInfo chatinfo = new TChatInfo();
		ProxiedPlayer player = (ProxiedPlayer)sender;

		chatinfo.setChatColor(args[0].charAt(0));
		chatinfo.setNameColor(args[1].charAt(0));

		ds.getAdminChatPreferences().remove(player.getUniqueId());
		ds.getAdminChatPreferences().put(player.getUniqueId(), chatinfo);

		DebugManager.log("[ACCCommand] Preferences updated");

		MessageManager.sendMessage(sender, "command_acc_updated");
	}
}
