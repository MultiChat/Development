package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;

/**
 * Group List Command
 * <p>Displays a list of all current group chats on the server</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GroupListCommand extends Command {

	public GroupListCommand() {
		super("mcgroups", "multichat.staff.listgroups", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("groups").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		MessageManager.sendMessage(sender, "command_grouplist_list");

		for (String groupname : ds.getGroupChats().keySet()) {
			MessageManager.sendSpecialMessage(sender, "command_grouplist_list_item", groupname);
		}

	}
}
