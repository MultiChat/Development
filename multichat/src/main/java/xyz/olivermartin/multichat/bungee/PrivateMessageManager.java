package xyz.olivermartin.multichat.bungee;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;

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

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		message = MultiChatUtil.reformatRGB(message);

		String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
		String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
		String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

		String finalmessage = chatfix.replaceMsgVars(messageoutformat, message, sender, target);
		if (MultiChat.legacyServers.contains(sender.getServer().getInfo().getName())) {
			sender.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
		} else {
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
		}

		finalmessage = chatfix.replaceMsgVars(messageinformat, message, sender, target);
		if (MultiChat.legacyServers.contains(target.getServer().getInfo().getName())) {
			target.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
		} else {
			target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
		}

		finalmessage = chatfix.replaceMsgVars(messagespyformat, message, sender, target);
		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != sender.getUniqueId())
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")
							|| target.hasPermission("multichat.staff.spy.bypass")))) {

				if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
				} else {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
				}

			}

		}

		if (ds.getLastMsg().containsKey(sender.getUniqueId())) {
			ds.getLastMsg().remove(sender.getUniqueId());
		}

		ds.getLastMsg().put(sender.getUniqueId(), target.getUniqueId());

		if (ds.getLastMsg().containsKey(target.getUniqueId())) {
			ds.getLastMsg().remove(target.getUniqueId());
		}

		ds.getLastMsg().put(target.getUniqueId(), sender.getUniqueId());

		ConsoleManager.logSocialSpy(sender.getName(), target.getName(), message);

	}

	public void sendMessageConsoleTarget(String message, ProxiedPlayer sender) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		message = MultiChatUtil.reformatRGB(message);

		String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
		String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
		String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

		String finalmessage = chatfix.replaceMsgConsoleTargetVars(messageoutformat, message, (ProxiedPlayer)sender);
		if (MultiChat.legacyServers.contains(sender.getServer().getInfo().getName())) {
			sender.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
		} else {
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
		}

		finalmessage = chatfix.replaceMsgConsoleTargetVars(messageinformat, message, (ProxiedPlayer)sender);
		ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgConsoleTargetVars(messagespyformat, message, (ProxiedPlayer)sender);
		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != ((ProxiedPlayer)sender).getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")))) {

				if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
				} else {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
				}
			}

		}

		if (ds.getLastMsg().containsKey(((ProxiedPlayer)sender).getUniqueId())) {
			ds.getLastMsg().remove(((ProxiedPlayer)sender).getUniqueId());
		}

		ds.getLastMsg().put(((ProxiedPlayer)sender).getUniqueId(), new UUID(0L, 0L));

		if (ds.getLastMsg().containsKey(new UUID(0L, 0L))) {
			ds.getLastMsg().remove(new UUID(0L, 0L));
		}

		ds.getLastMsg().put(new UUID(0L, 0L), ((ProxiedPlayer)sender).getUniqueId());

	}

	public void sendMessageConsoleSender(String message, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		message = MultiChatUtil.reformatRGB(message);

		CommandSender sender = ProxyServer.getInstance().getConsole();

		String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
		String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
		String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

		String finalmessage = chatfix.replaceMsgConsoleSenderVars(messageoutformat, message, target);
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

		finalmessage = chatfix.replaceMsgConsoleSenderVars(messageinformat, message, target);
		if (MultiChat.legacyServers.contains(target.getServer().getInfo().getName())) {
			target.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
		} else {
			target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
		}

		finalmessage = chatfix.replaceMsgConsoleSenderVars(messagespyformat, message, target);
		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(target.hasPermission("multichat.staff.spy.bypass")))) {

				if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', finalmessage))));
				} else {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));
				}
			}

		}

		if (ds.getLastMsg().containsKey(new UUID(0L, 0L))) {
			ds.getLastMsg().remove(new UUID(0L, 0L));
		}

		ds.getLastMsg().put(new UUID(0L, 0L), target.getUniqueId());

		if (ds.getLastMsg().containsKey(target.getUniqueId())) {
			ds.getLastMsg().remove(target.getUniqueId());
		}

		ds.getLastMsg().put(target.getUniqueId(), new UUID(0L, 0L));

	}

}
