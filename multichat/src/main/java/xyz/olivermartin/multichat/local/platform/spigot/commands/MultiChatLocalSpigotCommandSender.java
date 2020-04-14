package xyz.olivermartin.multichat.local.platform.spigot.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;

public class MultiChatLocalSpigotCommandSender implements MultiChatLocalCommandSender {

	private CommandSender sender;

	public MultiChatLocalSpigotCommandSender(CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public void sendGoodMessage(String message) {
		sender.sendMessage(ChatColor.GREEN + message);
	}

	@Override
	public void sendBadMessage(String message) {
		sender.sendMessage(ChatColor.DARK_RED + message);
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public boolean isPlayer() {
		return (sender instanceof Player);
	}

	@Override
	public void sendInfoMessageA(String message) {
		sender.sendMessage(ChatColor.DARK_AQUA + message);
	}

	@Override
	public void sendInfoMessageB(String message) {
		sender.sendMessage(ChatColor.AQUA + message);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

}
