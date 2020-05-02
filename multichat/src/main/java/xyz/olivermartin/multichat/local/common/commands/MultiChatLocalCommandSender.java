package xyz.olivermartin.multichat.local.common.commands;

public interface MultiChatLocalCommandSender {

	public void sendGoodMessage(String message);
	
	public void sendBadMessage(String message);
	
	public void sendInfoMessageA(String message);
	
	public void sendInfoMessageB(String message);
	
	public boolean hasPermission(String permission);
	
	public boolean isPlayer();
	
	public String getName();
	
}
