package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Context {

	private String id;
	private int priority;
	private String defaultChannel;
	private boolean forceChannel;
	private boolean blacklistServers;
	private List<String> servers;

	public Context(String id, int priority, String defaultChannel, boolean forceChannel, boolean blacklistServers, List<String> servers) {
		this.id = id;
		this.priority = priority;
		this.defaultChannel = defaultChannel;
		this.forceChannel = forceChannel;
		this.blacklistServers = blacklistServers;
		this.servers = servers;
	}

	public Context(String id, int priority, String defaultChannel, boolean forceChannel) {
		this.id = id;
		this.priority = priority;
		this.defaultChannel = defaultChannel;
		this.forceChannel = forceChannel;
		this.blacklistServers = true;
		this.servers = new ArrayList<String>();
	}

	public Context(String id, int priority, String defaultChannel) {
		this.id = id;
		this.priority = priority;
		this.defaultChannel = defaultChannel;
		this.forceChannel = false;
		this.blacklistServers = true;
		this.servers = new ArrayList<String>();
	}

	public Context(String id, int priority) {
		this.id = id;
		this.priority = priority;
		this.defaultChannel = "global";
		this.forceChannel = false;
		this.blacklistServers = true;
		this.servers = new ArrayList<String>();
	}

	/**
	 * Get the ID of this context
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get the priority of this context
	 * @return the priority
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * Get the default channel of this context
	 * @return the default channel
	 */
	public String getDefaultChannel() {
		return this.defaultChannel;
	}

	/**
	 * Are players forced into the default channel every time they enter the context?
	 * @return true if they are forced into the default channel
	 */
	public boolean isForceChannel() {
		return this.forceChannel;
	}

	/**
	 * Is the server list a blacklist (rather than a whitelist)
	 * @return true if the server list is a blacklist
	 */
	public boolean isBlacklistServers() {
		return this.blacklistServers;
	}

	/**
	 * <p>Gets the blacklist/whitelist of servers for this context.</p>
	 * <p>List is a blacklist if isBlacklistServers() returns true, otherwise is a whitelist</p>
	 * @return the blacklist/whitelist of servers
	 */
	public List<String> getServerList() {
		return this.servers;
	}

	/**
	 * <p>Checks if a command sender is contained by this context (on one of the allowed servers)</p>
	 * <p>Note: If the sender is not a player then it is assumed they are always in the context</p>
	 * @param sender The sender to check
	 * @return true if the sender is within the context
	 */
	public boolean contains(CommandSender sender) {

		if (!(sender instanceof ProxiedPlayer)) return true;

		ProxiedPlayer player = (ProxiedPlayer) sender;

		if (player.getServer() == null) return false;

		String server = player.getServer().getInfo().getName();

		if (blacklistServers) {
			return !servers.contains(server);
		} else {
			return servers.contains(server);
		}

	}

}
