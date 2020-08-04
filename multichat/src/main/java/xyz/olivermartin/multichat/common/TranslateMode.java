package xyz.olivermartin.multichat.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TranslateMode {

	COLOR_SIMPLE ("(?i)%1$s([a-f,0-9])"),
	COLOR_ALL ("(?i)%1$s([a-f,0-9,r,x])"),
	FORMAT_UNDERLINE ("(?i)%1$s([n])"),
	FORMAT_ITALIC ("(?i)%1$s([o])"),
	FORMAT_BOLD ("(?i)%1$s([l])"),
	FORMAT_STRIKE ("(?i)%1$s([m])"),
	FORMAT_OBFUSCATED ("(?i)%1$s([k])"),
	FORMAT_RESET ("(?i)%1$s([r])"),
	FORMAT_ALL ("(?i)%1$s([k-o,r])"),
	SIMPLE ("(?i)%1$s([a-f,0-9,k-o,r])"),
	ALL ("(?i)%1$s([a-f,0-9,k-o,r,x])"),
	X ("(?i)%1$s([x])");

	private static final String ORIGIN_CHAR = "&";
	private static final String TRANSLATED_CHAR = "§";

	private Pattern originPattern;
	private Pattern translatedPattern;

	private TranslateMode(String regex) {
		this.originPattern = Pattern.compile(String.format(regex, ORIGIN_CHAR));
		this.translatedPattern = Pattern.compile(String.format(regex, TRANSLATED_CHAR));
	}

	public static boolean isRGB(TranslateMode mode) {
		return mode.equals(ALL) ||  mode.equals(X) || mode.equals(COLOR_ALL);
	}

	public Pattern getOriginPattern() {
		return this.originPattern;
	}

	public Pattern getTranslatedPattern() {
		return this.translatedPattern;
	}

	public String translate(String rawMessage) {
		Matcher matcher = getOriginPattern().matcher(rawMessage);
		return matcher.replaceAll(TRANSLATED_CHAR + "$1");
	}

	public String revert(String rawMessage) {
		Matcher matcher = getTranslatedPattern().matcher(rawMessage);
		return matcher.replaceAll(ORIGIN_CHAR + "$1");
	}

	public boolean containsAny(String rawMessage) {
		return !rawMessage.equals(stripAll(rawMessage));
	}

	public boolean containsOrigin(String rawMessage) {
		return !rawMessage.equals(stripOrigin(rawMessage));
	}

	public boolean containsTranslated(String rawMessage) {
		return !rawMessage.equals(stripTranslated(rawMessage));
	}

	public String stripAll(String rawMessage) {
		return stripTranslated(stripOrigin(rawMessage));
	}

	public String stripTranslated(String rawMessage) {
		return strip(rawMessage, getTranslatedPattern());
	}

	public String stripOrigin(String rawMessage) {
		return strip(rawMessage, getOriginPattern());
	}

	private String strip(String rawMessage, Pattern pattern) {
		Matcher matcher = pattern.matcher(rawMessage);
		return matcher.replaceAll("");
	}

}
