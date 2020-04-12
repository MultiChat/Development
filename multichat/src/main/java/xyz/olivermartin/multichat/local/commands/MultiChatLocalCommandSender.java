package xyz.olivermartin.multichat.local.commands;

public interface MultiChatLocalCommandSender {

	public void sendGoodMessage(String message);
	
	public void sendBadMessage(String message);
	
	public boolean hasPermission(String permission);
	
	public boolean isPlayer();
	
}
