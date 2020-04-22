package xyz.olivermartin.multichat.proxy.common;

public class RegexRule {

	private String lookFor;
	private String replaceWith;

	public RegexRule(String lookFor, String replaceWith) {
		this.lookFor = lookFor;
		this.replaceWith = replaceWith;
	}

	/**
	 * @deprecated
	 * 
	 * THIS IS ONLY LISTED AS DEPRECATED TO MAKE ME CHECK THIS IS HOW IT IS NORMALLY IMPLEMENTED IN THE REGEX RULE
	 * MAKE SURE THIS WILL WORK!!!!!!!!!!
	 * 
	 * @param message
	 * @return
	 */
	@Deprecated
	public String apply(String message) {
		return message.replaceAll(lookFor, replaceWith);
	}

}
