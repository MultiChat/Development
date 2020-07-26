package xyz.olivermartin.multichat.proxy.common.channels.proxy;

import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

public class DynamicProxyChannel extends GenericProxyChannel {

	private boolean blacklistMembers; // Should member list of the channel be a blacklist or whitelist
	private Set<UUID> members; // Member list of the channel

	public DynamicProxyChannel(String id, ProxyChannelInfo info, ChannelManager manager, boolean blacklistMembers, Set<UUID> members) {
		super(id, info, manager);
		this.blacklistMembers = blacklistMembers;
		this.members = members;
	}

	/**
	 * Checks if the command sender is a member of this channel
	 * @param sender The command sender to check
	 * @return true if they are a member of the channel
	 */
	public boolean isMember(CommandSender sender) {

		// Console always member
		if (!(sender instanceof ProxiedPlayer)) return true;

		ProxiedPlayer player = (ProxiedPlayer) sender;

		if (blacklistMembers) {
			return !members.contains(player.getUniqueId());
		} else {
			return members.contains(player.getUniqueId());
		}

	}

	/**
	 * Is the member list for this channel a blacklist or a whitelist?
	 * @return true if it is a blacklist
	 */
	public boolean isBlacklistMembers() {
		return this.blacklistMembers;
	}

	/**
	 * Controls if the member list for this channel is a blacklist or a whitelist
	 * @param blacklistMembers TRUE for blacklist, FALSE for whitelist
	 */
	public void setBlacklistMembers(boolean blacklistMembers) {
		this.blacklistMembers = blacklistMembers;
	}

	/**
	 * <p>Gets the blacklist/whitelist of members for this channel.</p>
	 * <p>List is a blacklist if isBlacklistMembers() returns true, otherwise is a whitelist</p>
	 * @return the blacklist/whitelist of members
	 */
	public Set<UUID> getMembers() {
		return this.members;
	}

	@Override
	public boolean canSpeak(CommandSender sender) {
		return isMember(sender) && getInfo().inContext(sender) && getInfo().hasSpeakPermission(sender);
	}

	@Override
	public boolean canView(CommandSender sender) {
		return isMember(sender) && getInfo().inContext(sender) && getInfo().hasViewPermission(sender);
	}

}
