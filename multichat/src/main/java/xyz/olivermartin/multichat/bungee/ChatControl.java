package xyz.olivermartin.multichat.bungee;

import java.util.List;
import java.util.Map;

import net.md_5.bungee.config.Configuration;

public class ChatControl {

	@SuppressWarnings("rawtypes")
	public static String applyChatRules(String input, String chatType) {

		// TODO Actually register this config file in the code etc.
		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (!config.contains("apply_rules_to." + chatType)) return input;
		if (!config.getBoolean("apply_rules_to." + chatType)) return input;

		List rules = config.getList("regex_rules");

		if (rules != null) {

			for (Object rule : rules) {

				Map dictionary = (Map) rule;
				input = input.replaceAll(String.valueOf( dictionary.get("look_for")), String.valueOf(dictionary.get("replace_with") ));

			}

		}

		return input;
	}

}
