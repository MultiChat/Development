package xyz.olivermartin.multichat.spigotbridge;

import java.util.List;
import java.util.UUID;

public class PseudoChannel {
	
	public PseudoChannel(String name, List<UUID> members, boolean whitelist) {
		
		this.name = name;
		this.whitelistMembers = whitelist;
		this.members = members;
		
	}

	public String name;
	public boolean whitelistMembers;
	public  List<UUID> members;
	
}
