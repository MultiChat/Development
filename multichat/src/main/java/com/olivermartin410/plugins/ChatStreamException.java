package com.olivermartin410.plugins;

public class ChatStreamException extends Exception {

	private static final long serialVersionUID = 1L;
	String info;
	
	public ChatStreamException(String info) {
		this.info = info;
	}
	
	@Override
	public String toString() {
		return info;
	}
	
}
