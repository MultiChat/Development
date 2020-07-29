package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;

public class MuteCommand extends Command {

	public MuteCommand() {
		super("mcmute", "multichat.mute", (String[])ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("mute").toArray(new String[0]));
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!ConfigManager.getInstance().getHandler(ConfigFile.CHAT_CONTROL).getConfig().getBoolean("mute")) return;
		
		if (args.length != 1) {
			
			MessageManager.sendMessage(sender, "mute_usage");
			
		} else {
			
			String username = args[0];
			
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(username);
			
			if (target != null) {
				
				if (target.hasPermission("multichat.mute.bypass")) {
					MessageManager.sendMessage(sender, "mute_bypass");
					return;
				}
				
				if (!ChatControl.isMutedAnywhere(target.getUniqueId())) {
					ChatControl.mute(target.getUniqueId());
					MessageManager.sendMessage(sender, "mute_muted_staff");
					MessageManager.sendMessage(target, "mute_muted");
				} else {
					ChatControl.unmute(target.getUniqueId());
					MessageManager.sendMessage(sender, "mute_unmuted_staff");
					MessageManager.sendMessage(target, "mute_unmuted");
				}
				
			} else {
				
				MessageManager.sendMessage(sender, "mute_player_not_found");
				
			}
			
		}
		
	}

}
