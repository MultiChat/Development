package xyz.olivermartin.multichat.bungee;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PrivateMessageManager {

	private static PrivateMessageManager instance;

	public static PrivateMessageManager getInstance() {
		return instance;
	}

	static {
		instance = new PrivateMessageManager();
	}

	/* END STATIC */

	private ChatManipulation chatfix;

	private PrivateMessageManager() { 
		chatfix = new ChatManipulation();
	}

	public void sendMessage(String message, ProxiedPlayer sender, ProxiedPlayer target) {

		String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
		String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
		String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

		String finalmessage = chatfix.replaceMsgVars(messageoutformat, message, sender, target);
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgVars(messageinformat, message, sender, target);
		target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgVars(messagespyformat, message, sender, target);
		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (MultiChat.socialspy.contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != sender.getUniqueId())
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")
							|| target.hasPermission("multichat.staff.spy.bypass")))) {

				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
			}

		}

		if (MultiChat.lastmsg.containsKey(sender.getUniqueId())) {
			MultiChat.lastmsg.remove(sender.getUniqueId());
		}

		MultiChat.lastmsg.put(sender.getUniqueId(), target.getUniqueId());

		if (MultiChat.lastmsg.containsKey(target.getUniqueId())) {
			MultiChat.lastmsg.remove(target.getUniqueId());
		}

		MultiChat.lastmsg.put(target.getUniqueId(), sender.getUniqueId());

		ConsoleManager.logSocialSpy(sender.getName(), target.getName(), message);

	}

	public void sendMessageConsoleTarget(String message, ProxiedPlayer sender) {

		String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
		String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
		String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

		String finalmessage = chatfix.replaceMsgConsoleTargetVars(messageoutformat, message, (ProxiedPlayer)sender);
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgConsoleTargetVars(messageinformat, message, (ProxiedPlayer)sender);
		ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgConsoleTargetVars(messagespyformat, message, (ProxiedPlayer)sender);
		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (MultiChat.socialspy.contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != ((ProxiedPlayer)sender).getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")))) {

				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
			}

		}

		if (MultiChat.lastmsg.containsKey(((ProxiedPlayer)sender).getUniqueId())) {
			MultiChat.lastmsg.remove(((ProxiedPlayer)sender).getUniqueId());
		}

		MultiChat.lastmsg.put(((ProxiedPlayer)sender).getUniqueId(), new UUID(0L, 0L));

		if (MultiChat.lastmsg.containsKey(new UUID(0L, 0L))) {
			MultiChat.lastmsg.remove(new UUID(0L, 0L));
		}

		MultiChat.lastmsg.put(new UUID(0L, 0L), ((ProxiedPlayer)sender).getUniqueId());

	}

	public void sendMessageConsoleSender(String message, ProxiedPlayer target) {

		CommandSender sender = ProxyServer.getInstance().getConsole();

		String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
		String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
		String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

		String finalmessage = chatfix.replaceMsgConsoleSenderVars(messageoutformat, message, target);
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgConsoleSenderVars(messageinformat, message, target);
		target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgConsoleSenderVars(messagespyformat, message, target);
		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (MultiChat.socialspy.contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(target.hasPermission("multichat.staff.spy.bypass")))) {

				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
			}

		}

		if (MultiChat.lastmsg.containsKey(new UUID(0L, 0L))) {
			MultiChat.lastmsg.remove(new UUID(0L, 0L));
		}

		MultiChat.lastmsg.put(new UUID(0L, 0L), target.getUniqueId());

		if (MultiChat.lastmsg.containsKey(target.getUniqueId())) {
			MultiChat.lastmsg.remove(target.getUniqueId());
		}

		MultiChat.lastmsg.put(target.getUniqueId(), new UUID(0L, 0L));

	}

}
