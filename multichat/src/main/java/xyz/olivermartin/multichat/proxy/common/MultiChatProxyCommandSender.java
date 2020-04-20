package xyz.olivermartin.multichat.proxy.common;

public interface MultiChatProxyCommandSender {

	public void sendPlainMessage(String message);

	public void sendColouredMessage(String message);

	public void sendGoodMessage(String message);

	public void sendBadMessage(String message);

	public void sendTealInfoMessage(String message);

	public void sendAquaInfoMessage(String message);

	public void sendForestInfoMessage(String message);

	public boolean hasProxyPermission(String permission);

	public boolean isPlayer();

	public String getName();

}
