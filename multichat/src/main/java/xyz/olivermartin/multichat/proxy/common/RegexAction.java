package xyz.olivermartin.multichat.proxy.common;

public class RegexAction {

	@SuppressWarnings("unused")
	private String lookFor;
	@SuppressWarnings("unused")
	private String command;
	@SuppressWarnings("unused")
	private boolean cancel;
	@SuppressWarnings("unused")
	private boolean local;

	public RegexAction(String lookFor, String command, boolean cancel, boolean local) {
		this.lookFor = lookFor;
		this.command = command;
		this.cancel = cancel;
		this.local = local;
	}

	/**
	 * @deprecated
	 * 
	 * THIS IS ONLY LISTED AS DEPRECATED BECAUSE IT HAS NOT YET BEEN IMPLEMENTED!!!!!!!!!!!!!!!
	 * 
	 * @param message
	 * @return
	 */
	@Deprecated
	public String apply(String message) {
		// TODOreturn message.replaceAll(lookFor, replaceWith);
		return "";
	}

}
