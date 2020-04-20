package xyz.olivermartin.multichat.proxy.common;

import java.util.UUID;

public interface MultiChatProxyPlayer extends MultiChatProxyCommandSender {

	public UUID getUniqueId();

	public String getPrefix();

	public String getSuffix();

	public String getDisplayName();

	public String getWorld();

	public String getServer();

	public String getCurrentName();

}
