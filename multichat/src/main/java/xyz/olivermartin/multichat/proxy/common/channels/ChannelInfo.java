package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.List;
import java.util.Optional;

import net.md_5.bungee.api.CommandSender;

public class ChannelInfo {

	private String desc; // A short description of the channel
	private String format; // The format of this channel
	private boolean unhideable; // If the channel is unhideable
	private Context context; // The context for this channel
	private List<String> aliases; // The command aliases for this channel

	private String permission; // Permission to view / speak
	private String viewPermission; // Permission to view only

	public ChannelInfo(String desc, String format, boolean unhideable, Context context, List<String> aliases) {

		this.desc = desc;
		this.format = format;
		this.unhideable = unhideable;
		this.context = context;
		this.aliases = aliases;

		this.permission = null;
		this.viewPermission = null;

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

	/*
	 * Are any permissions set for this channel?
	 */
	private boolean isPermissionProtected() {
		return this.permission != null;
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
	 * Checks if the command sender has permission to view this channel
	 * @param sender The command sender to check
	 * @return true if they have permission to view the channel
	 */
	public boolean hasViewPermission(CommandSender sender) {
		if (!isPermissionProtected()) return true;
		return sender.hasPermission(permission) || sender.hasPermission(viewPermission);
	}

	/**
	 * Checks if the command sender has permission to speak in channel
	 * @param sender The command sender to check
	 * @return true if they have permission to speak into the channel
	 */
	public boolean hasSpeakPermission(CommandSender sender) {
		if (!isPermissionProtected()) return true;
		return sender.hasPermission(permission);
	}

	/**
	 * Checks if the command sender is in the context of the channel
	 * @param sender The command sender to check
	 * @return true if they are in the context
	 */
	public boolean inContext(CommandSender sender) {
		return context.contains(sender);
	}

}
