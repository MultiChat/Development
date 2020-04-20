package xyz.olivermartin.multichat.proxy.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyCommandSender;

public class MultiChatProxyBungeeCommandSender implements MultiChatProxyCommandSender {

	private CommandSender sender;

	public MultiChatProxyBungeeCommandSender(CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public void sendPlainMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(message));
	}

	@Override
	public void sendColouredMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',message)));
	}

	@Override
	public void sendGoodMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&a" + message)));
	}

	@Override
	public void sendBadMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&c" + message)));
	}

	@Override
	public void sendTealInfoMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&3" + message)));
	}

	@Override
	public void sendAquaInfoMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&b" + message)));
	}

	@Override
	public void sendForestInfoMessage(String message) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',"&2" + message)));
	}

	@Override
	public boolean hasProxyPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public boolean isPlayer() {
		return (sender instanceof ProxiedPlayer);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

}
