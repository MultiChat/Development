package xyz.olivermartin.multichat.proxy.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TranslateMode {

	COLOUR_SIMPLE ("(?i)&(?=[a-f,0-9])"),
	COLOUR_ALL ("(?i)&(?=[a-f,0-9,x])"),
	FORMAT_UNDERLINE ("(?i)&(?=[n])"),
	FORMAT_ITALIC ("(?i)&(?=[o])"),
	FORMAT_BOLD ("(?i)&(?=[l])"),
	FORMAT_STRIKE ("(?i)&(?=[m])"),
	FORMAT_OBFUSCATED ("(?i)&(?=[k])"),
	FORMAT_RESET ("(?i)&(?=[r])"),
	FORMAT_ALL ("(?i)&(?=[k-o,r])"),
	ALL ("(?i)&(?=[a-f,0-9,k-o,r,x])");

	private Pattern pattern;

	private TranslateMode(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public String translate(String rawMessage) {
		Matcher matcher = pattern.matcher(rawMessage);
		return matcher.replaceAll("§");
	}

}
