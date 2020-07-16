package xyz.olivermartin.multichat.bungee;

import java.util.UUID;

public class PlayerMeta {

	public UUID uuid;
	public String name;
	public String nick;
	public String localDisplayName;
	public String prefix;
	public String suffix;
	public String world;

	public PlayerMeta(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		nick = name;
		localDisplayName = nick;
		prefix = "";
		suffix = "";
		world = "";
	}
	
	public String getSpigotDisplayname() {
		return this.localDisplayName;
	}
	
	/*public String getDisplayName(String format) {
		
		String displayName = format;
		
		displayName = displayName.replaceAll("%NAME%", name);
		displayName = displayName.replaceAll("%PREFIX%", prefix);
		displayName = displayName.replaceAll("%SUFFIX%", suffix);
		displayName = displayName.replaceAll("%NICK%", nick);
		displayName = displayName.replaceAll("%UUID%", uuid.toString());
		
		displayName = displayName.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");
		
		return displayName;
		
	}*/

}
