package xyz.olivermartin.multichat.proxy.common.channels;

import net.md_5.bungee.api.CommandSender;

public class StaticProxyChannel extends GenericProxyChannel {

	/*public static class Builder {

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

		public Builder(String id) {

			this.id = id;
			this.desc = id;
			this.format = "[" + id + "] %DISPLAYNAME%&f: ";
			this.unhideable = false;
			this.context = MultiChatProxy.getInstance().getContextManager().getGlobalContext();
			this.aliases = new ArrayList<String>();

			this.permission = null;
			this.viewPermission = null;

			this.blacklistMembers = true;
			this.members = new HashSet<UUID>();

		}

		public Builder withDescription(String desc) {
			this.desc = desc;
			return this;
		}

		public Builder withFormat(String format) {
			this.format = format;
			return this;
		}

		public Builder isUnhideable(boolean unhideable) {
			this.unhideable = unhideable;
			return this;
		}

		public Builder inContext(Context context) {
			this.context = context;
			return this;
		}

		public Builder withAliases(List<String> aliases) {
			this.aliases = aliases;
			return this;
		}

		public Builder withPemission(String permission) {
			this.permission = permission;
			return this;
		}

		public Builder withViewPemission(String viewPermission) {
			this.viewPermission = viewPermission;
			return this;
		}

		public Builder isBlacklistMembers(boolean blacklistMembers) {
			this.blacklistMembers = blacklistMembers;
			return this;
		}

		public Builder withMemberList(Set<UUID> members) {
			this.members = members;
			return this;
		}

		public StaticNetworkChannel build() {
			return new StaticNetworkChannel(id, desc, format, unhideable, context, aliases, permission, viewPermission);
		}

	}*/

	public StaticProxyChannel(String id, ProxyChannelInfo info, ChannelManager manager) {
		super(id, info, manager);
	}

	@Override
	public boolean canSpeak(CommandSender sender) {
		return getInfo().inContext(sender) && getInfo().hasSpeakPermission(sender);
	}

	@Override
	public boolean canView(CommandSender sender) {
		return getInfo().inContext(sender) && getInfo().hasViewPermission(sender);
	}

}
