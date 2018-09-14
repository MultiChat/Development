package xyz.olivermartin.multichat.bungee;

import java.util.List;
import java.util.Map;

import net.md_5.bungee.config.Configuration;

public class ChatControl {

	@SuppressWarnings("rawtypes")
	public static String applyChatRules(String input) {

		// TODO Actually register this config file in the code etc.
		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();
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
