package com.olivermartin410.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpongeCommHandler extends Thread {

	protected String name;
    protected Socket socket;
    protected BufferedReader in;
    protected PrintWriter out;
    protected SpongeCommServer server;

    public SpongeCommHandler(Socket socket, SpongeCommServer server) {
        this.socket = socket;
        this.server = server;
    }
    //Set the socket, and the connected server class

    public void run() {
        try {

            // Create character streams for the socket.
        	
        	//Input stream
            in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
            //Output PrintWriter
            out = new PrintWriter(socket.getOutputStream(), true);

            // Keep requesting the connection to enter a name
            // NB: We must lock the set of names while doing this!
            
            boolean nameAdded = false;
            while (!nameAdded) {
                out.println("ENTERNAME"); //Send ENTERNAME request to client
                name = in.readLine(); //Set name to the next line of the input stream
                if (name == null) {
                    return; //If the connected is terminated and the input stream no longer gets a result then terminate this runnable
                }
                //Lock the set of client names
                synchronized (server.clientNames) {
                    if (!server.clientNames.contains(name)) {
                    	//If it is a unique name then add it to the list and exit this loop
                        server.clientNames.add(name);
                        nameAdded = true;
                    }
                }
            }

            // The user is now accepted by our system
            // Add a new printWriter for this client to the hashset so we can send messages
            
            out.println("NAMEACCEPTED"); //Send NAMEACCEPTED command
            server.clientWriters.add(out); //Add the print writer
            
            // Accept messages from this client and broadcast them.
            // Ignore other clients that cannot be broadcasted to.
            
            while (true) {
                String input = in.readLine();
                if (input == null) {
                    return; //If the input stream no longer receives a result then the connection is lost and terminate this runnable
                }
                //For all the print writers in the list send the MESSAGE command with their message
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(input.split("<<&>>")[0]);
                p.setDisplayName(input.split("<<&>>")[1]);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            // This client is going down!  Remove its name and its print
            // writer from the sets, and close its socket.
            if (name != null) {
            	for (PrintWriter writer : server.clientWriters) {
                	writer.println("INFO " + name + " has left the chat!");
                }
            	System.out.println("INFO: " + name + " has left the chat!");
                server.clientNames.remove(name); //If they had entered a name then remove it from the list
            }
            if (out != null) {
                server.clientWriters.remove(out); //If the PrintWriter was created before disconnection then remove it from the list
            }
            try {
                socket.close(); //Close the socket if possible
            } catch (IOException e) {
            }
        }
} 
	
}
