package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageManager {
	
	private static Map<String,String> messages;
	
	public MessageManager() {
		
		messages = new HashMap<String,String>();
		
		// COMMAND_ACC
		messages.put("command_acc_usage", "&aUsage: /acc <chatcolorcode> <namecolorcode>");
		messages.put("command_acc_only_players", "&cOnly players can change chat colours!");
		messages.put("command_acc_updated", "&aAdmin-Chat colours updated!");
		messages.put("command_acc_invalid", "&cInvalid color codes specified: Must be letters a-f or numbers 0-9");
		messages.put("command_acc_invalid_usage", "&cUsage: /acc <chatcolorcode> <namecolorcode>");
		
		// COMMAND_AC
		messages.put("command_ac_toggle_on", "&dAdmin chat toggled on!");
		messages.put("command_ac_toggle_off", "&cAdmin chat toggled off!");
		messages.put("command_ac_only_players", "&cOnly players can toggle the chat!");
		
	}

	public static String getMessage(String id) {
		if (!messages.containsKey(id)) return "&8[&2Multi&aChat&8] &cNo message defined for: " + id;
		return messages.get(id.toLowerCase());
	}
	
	public static void sendMessage(CommandSender sender, String id) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', getMessage(id))));
	}
	
}
