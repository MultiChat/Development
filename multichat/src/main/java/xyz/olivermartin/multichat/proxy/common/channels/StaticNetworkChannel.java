package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class StaticNetworkChannel extends NetworkChannel {

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

	public StaticNetworkChannel(String id, String desc, String format, boolean unhideable, Context context, List<String> aliases, String permission, String viewPermission) {
		// Create NetworkChannel with a "blacklist of members" that will always be empty (as this is a static channel)
		super(id, desc, format, unhideable, context, aliases, permission, viewPermission, true, new HashSet<UUID>());
	}

}
