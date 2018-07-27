package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class Announcements {

	private static Map<String, Integer> AKey = new HashMap<String, Integer>();
	private static Map<String, String> Announcements = new HashMap<String, String>();
	
	public static boolean startAnnouncement(final String name, Integer minutes) {
			
			if(!(AKey.containsKey(name.toLowerCase())) && Announcements.containsKey(name.toLowerCase())) {
			
			Integer ID;
			
			ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(MultiChat.getInstance(), new Runnable()
		    {
			  @Override
		      public void run()
		      {
		        String message;
		        String URLBIT;
		    	ChatManipulation chatman = new ChatManipulation();
		    	 message = chatman.FixFormatCodes(Announcements.get(name.toLowerCase()));
		    	  URLBIT = chatman.getURLBIT(Announcements.get(name.toLowerCase()));
		    	  
		    	  for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
						onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
					}
		    	  
		      }
				
		    }, 0, minutes, TimeUnit.MINUTES);
			
			ID = task.getId();

			AKey.put(name.toLowerCase(), ID);
			
			return true;
			
			} else {
				
				return false;
				
			}
		
	}
	
	public static HashMap<String,String> getAnnouncementList() {
		
		return (HashMap<String, String>) Announcements;
		
	}
	
	public static void loadAnnouncementList(HashMap<String, String> loadedAnnouncements) {
		
		Announcements = loadedAnnouncements;
		
	}
	
	public static boolean stopAnnouncement(String name) {
		
		if (AKey.containsKey(name.toLowerCase())) {
			ProxyServer.getInstance().getScheduler().cancel(AKey.get(name.toLowerCase()));
			AKey.remove(name.toLowerCase());
			return true;
		} else {
			return false;
		}
		
	}
	
	public static boolean addAnnouncement(String name, String message) {
		
		if (!Announcements.containsKey(name.toLowerCase())) {
	
		Announcements.put(name.toLowerCase(), message);
		
		return true;
		
		} else {
			
			return false;
			
		}
		
	}
	
	public static boolean removeAnnouncement(String name) {
		
		if (AKey.containsKey(name.toLowerCase())) {
			ProxyServer.getInstance().getScheduler().cancel(AKey.get(name.toLowerCase()));
			AKey.remove(name.toLowerCase());
		}
		if(Announcements.containsKey(name.toLowerCase())) {
			Announcements.remove(name.toLowerCase());
			return true;
		} else {
			return false;
		}
		
	}
	
	public static boolean existsAnnouncemnt(String name) {
		
		if ( Announcements.containsKey(name.toLowerCase() ) ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public static void playAnnouncement(String name) {
		
		if (Announcements.containsKey(name.toLowerCase())) {
		String message;
        String URLBIT;
    	ChatManipulation chatman = new ChatManipulation();
    	 message = chatman.FixFormatCodes(Announcements.get(name.toLowerCase()));
    	  URLBIT = chatman.getURLBIT(Announcements.get(name.toLowerCase()));
    	  
    	  for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
			}
		}
	}
	
}
