package com.olivermartin410.plugins;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Group Chat Info Class
 * <p>Stores information regarding a particular group chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class TGroupChatInfo extends TChatInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Set<UUID> members = new HashSet<>();
	private final Set<UUID> viewers = new HashSet<>();
	private final Set<UUID> admins = new HashSet<>();
	private final Set<UUID> bannedPlayers = new HashSet<>();

	private String PartyName;
	private boolean secret;
	private String password;
	private boolean formal;

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFormal(boolean formal) {
		this.formal = formal;
	}

	public boolean getFormal() {
		return this.formal;
	}

	public String getPassword() {
		return this.password;
	}

	public void setSecret(boolean secret) {
		this.secret = secret;
	}

	public boolean getSecret() {
		return this.secret;
	}

	public String getName() {
		return this.PartyName;
	}

	public void setName(String Name) {
		this.PartyName = Name;
	}

	public Set<UUID> getMembers() {
		return this.members;
	}

	public void addMember(UUID playerUID) {
		this.members.add(playerUID);
	}

	public void delMember(UUID playerUID) {
		this.members.remove(playerUID);
	}

	public Set<UUID> getAdmins() {
		return this.admins;
	}

	public void addAdmin(UUID playerUID) {
		this.admins.add(playerUID);
	}

	public void delAdmin(UUID playerUID) {
		this.admins.remove(playerUID);
	}

	public boolean isAdmin(UUID playerUID) {
		return this.admins.contains(playerUID);
	}

	public Set<UUID> getBanned() {
		return this.bannedPlayers;
	}

	public void addBanned(UUID playerUID) {
		this.bannedPlayers.add(playerUID);
	}

	public void delBanned(UUID playerUID) {
		this.bannedPlayers.remove(playerUID);
	}

	public boolean isBanned(UUID playerUID) {
		return this.bannedPlayers.contains(playerUID);
	}

	public boolean isMember(UUID playerUID) {
		return this.members.contains(playerUID);
	}

	public Set<UUID> getViewers() {
		return this.viewers;
	}

	public void addViewer(UUID playerUID) {
		this.viewers.add(playerUID);
	}

	public void delViewer(UUID playerUID) {
		this.viewers.remove(playerUID);
	}

	public boolean isViewer(UUID playerUID) {
		return this.viewers.contains(playerUID);
	}
}
