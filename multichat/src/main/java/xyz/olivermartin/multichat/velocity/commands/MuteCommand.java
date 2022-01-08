package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.ChatControl;
import xyz.olivermartin.multichat.velocity.ConfigManager;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;

public class MuteCommand extends Command {

	public MuteCommand() {
		super("multichatmute", ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getNode("mutecommand").getList(String::valueOf).toArray(new String[0]));
	}

	public boolean hasPermission(Invocation invocation) {
		return invocation.source().hasPermission("multichat.mute");
	}

	public void execute(Invocation invocation) {
		var args = invocation.arguments();
		var sender = invocation.source();

		if (!ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getNode("mute").getBoolean()) return;
		
		if (args.length != 1) {
			
			MessageManager.sendMessage(sender, "mute_usage");
			
		} else {
			
			String username = args[0];
			
			Player target = MultiChat.getInstance().getServer().getPlayer(username).orElse(null);
			
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
