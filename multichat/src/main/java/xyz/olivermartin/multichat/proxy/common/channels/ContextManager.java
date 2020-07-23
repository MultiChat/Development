package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.md_5.bungee.api.connection.ProxiedPlayer;

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

	public Optional<Context> getContext(String id) {
		return Optional.ofNullable(contexts.get(id));
	}

	public Context getContext(ProxiedPlayer player) {

		int lastPriority = -1;
		Context lastContext = null;

		for (Context c : contexts.values()) {
			if (c.contains(player)) {
				if (c.getPriority() > lastPriority) {
					lastContext = c;
					lastPriority = c.getPriority();
				}
			}
		}

		return lastContext;

	}

}
