package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;

public abstract class NetworkChannel {

	private String id; // The channel ID
	private String desc; // A short description of the channel
	private String format; // The format of this channel
	private boolean unhideable; // If the channel is unhideable
	private Context context; // The context for this channel
	private List<String> aliases; // The command aliases for this channel

	private String permission; // Permission to view / speak
	private String viewPermission; // Permission to view only

	private boolean blacklistMembers; // Should member list of the channel be a blacklist or whitelist
	private Set<UUID> members; // Member list of the channel

	public NetworkChannel(String id, String desc, String format, boolean unhideable, Context context, List<String> aliases, String permission, String viewPermission, boolean blacklistMembers, Set<UUID> members) {

		this.id = id;
		this.desc = desc;
		this.format = format;
		this.unhideable = unhideable;
		this.context = context;
		this.aliases = aliases;

		this.permission = null;
		this.viewPermission = null;

		this.blacklistMembers = blacklistMembers;
		this.members = members;

	}

	/**
	 * Gets the ID of this channel
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Gets a short description of this channel
	 * @return the description
	 */
	public String getDescription() {
		return this.desc;
	}

	/**
	 * Gets the format used for chat in this channel
	 * @return the format
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Checks if this channel is not allowed to be hidden
	 * @return true if the channel can not be hidden
	 */
	public boolean isUnhideable() {
		return this.unhideable;
	}

	/**
	 * Gets the context of this channel
	 * @return the context
	 */
	public Context getContext() {
		return this.context;
	}

	/**
	 * Gets the command aliases of this channel
	 * @return the command aliases
	 */
	public List<String> getAliases() {
		return this.aliases;
	}

	/**
	 * Checks if this channel requires a permission to view / speak
	 * @return true if the channel requires a permission
	 */
	public boolean isPermissionProtected() {
		return this.permission == null;
	}

	/**
	 * Gets the permission required to view / speak in the channel (if one exists)
	 * @return an optional of the permission
	 */
	public Optional<String> getPermission() {
		return Optional.ofNullable(permission);
	}

	/**
	 * Gets the permission required to ONLY view the channel (if one exists)
	 * @return an optional of the permission
	 */
	public Optional<String> getViewPermission() {
		return Optional.ofNullable(viewPermission);
	}

	/**
	 * Checks if this command sender is allowed to speak in the channel
	 * @param sender The command sender
	 * @return true if they are allowed to speak
	 */
	public boolean canSpeak(CommandSender sender) {
		if (!isPermissionProtected()) return true;
		return sender.hasPermission(permission);
	}

	/**
	 * Checks if this command sender is allowed to view the channel
	 * @param sender The command sender
	 * @return true if they are allowed to view
	 */
	public boolean canView(CommandSender sender) {
		if (!isPermissionProtected()) return true;
		return sender.hasPermission(permission) || sender.hasPermission(viewPermission);
	}

	/**
	 * Is the member list for this channel a blacklist or a whitelist?
	 * @return true if it is a blacklist
	 */
	public boolean isBlacklistMembers() {
		return this.blacklistMembers;
	}

	/**
	 * <p>Gets the blacklist/whitelist of members for this channel.</p>
	 * <p>List is a blacklist if isBlacklistMembers() returns true, otherwise is a whitelist</p>
	 * @return the blacklist/whitelist of members
	 */
	public Set<UUID> getMembers() {
		return this.members;
	}

}
