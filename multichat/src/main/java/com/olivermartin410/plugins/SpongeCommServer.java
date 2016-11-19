package com.olivermartin410.plugins;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;

public class SpongeCommServer {

	protected final int PORT = 25410; //Connection port to server
	protected HashSet<String> clientNames = new HashSet<String>(); //All of the connected user names (duplicates not allowed)
	protected HashSet<PrintWriter> clientWriters = new HashSet<PrintWriter>(); //All the writer objects to each client used to distribute messages
	
	public SpongeCommServer() {
		SpongeCommServer.main();
	}
	
	public static void main() {
		
		SpongeCommServer server = new SpongeCommServer(); //Create a new chat server
		System.out.println("TEA PARTY SERVER ACTIVATED (PORT:" + server.PORT + ")");
		
		//Create a new socket connection listener on the default port for this server
        ServerSocket listener = null;
		try {
			listener = new ServerSocket(server.PORT);
			while (true) {
                try {
					new SpongeCommHandler(listener.accept(),server).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}catch (Exception e) {
		} finally {
        	//Will also occur if an interrupt occurs to end execution
            try {
				listener.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //Prevent memory leaks!
        }
}
}
