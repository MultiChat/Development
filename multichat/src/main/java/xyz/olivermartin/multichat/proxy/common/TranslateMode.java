package xyz.olivermartin.multichat.proxy.common;

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

	private String regex;

	private TranslateMode(String regex) {
		this.regex = regex;
	}

	public String getRegex() {
		return this.regex;
	}

	public String translate(String rawMessage) {
		return rawMessage.replaceAll(this.regex,  "§");
	}

}
