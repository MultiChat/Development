package xyz.olivermartin.multichat.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TranslateMode {

	COLOUR_SIMPLE ("(?i)&([a-f,0-9,r])"),
	COLOUR_ALL ("(?i)&([a-f,0-9,r,x])"),
	FORMAT_UNDERLINE ("(?i)&([r,n])"),
	FORMAT_ITALIC ("(?i)&([r,o])"),
	FORMAT_BOLD ("(?i)&([r,l])"),
	FORMAT_STRIKE ("(?i)&([r,m])"),
	FORMAT_OBFUSCATED ("(?i)&([r,k])"),
	FORMAT_ALL ("(?i)&([k-o,r])"),
	SIMPLE ("(?i)&([a-f,0-9,k-o,r])"),
	ALL ("(?i)&([a-f,0-9,k-o,r,x])");

	private Pattern pattern;

	private TranslateMode(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public String translate(String rawMessage) {
		Matcher matcher = pattern.matcher(rawMessage);
		return matcher.replaceAll("§$1");
	}

}
