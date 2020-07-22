package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.HashMap;
import java.util.Map;

public class ContextManager {

	private Map<String, Context> contexts;
	private GlobalContext global;

	public ContextManager(GlobalContext global) {
		contexts = new HashMap<String, Context>();
		this.global = global;
		contexts.put("global", global);
	}

	public Context getGlobalContext() {
		return this.global;
	}

	public void setGlobalContext(GlobalContext global) {
		this.global = global;
		contexts.remove("global");
		contexts.put("global", global);
	}

}
