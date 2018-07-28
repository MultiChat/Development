package com.olivermartin410.plugins;

import java.io.Serializable;

/**
 * Chat Info Class
 * <p>Used to store information about chat info for mod and admin chats</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class TChatInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private char chatcolor;
	private char namecolor;

	public char getChatColor() {
		return this.chatcolor;
	}

	public void setChatColor(char color) {
		this.chatcolor = color;
	}

	public char getNameColor() {
		return this.namecolor;
	}

	public void setNameColor(char color) {
		this.namecolor = color;
	}
}
