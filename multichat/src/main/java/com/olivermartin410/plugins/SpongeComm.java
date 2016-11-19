package com.olivermartin410.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.entity.living.player.Player;

@Plugin(id = "multichat", name = "MultiChat - Sponge Bridge", version = "1.0")
public final class SpongeComm {
	protected BufferedReader in;
	protected PrintWriter out;
	int namesTried = 0;
	
	public void run() throws UnknownHostException, IOException {
	
	String serverAddress = "localhost"; //getServerAddress();
    Socket socket = new Socket(serverAddress, 25410); //Create a new socket to the server
    //Define the input and output streams
    in = new BufferedReader(new InputStreamReader(
        socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    // Process all messages from server, according to the protocol.
    while (true) {
        String line = in.readLine();
        if (line.startsWith("ENTERNAME")) {
            out.println(namesTried);
            namesTried++;
            //Send the result of the getName dialogue box
        } else if (line.startsWith("NAMEACCEPTED")) {
            //If the name is accepted then allow them to type a message
        } else if (line.startsWith("INFO")) {
        	String player = line.substring(5);
        	Optional<Player> p = Sponge.getServer().getPlayer(player);
        	out.write(p.get().getDisplayNameData().displayName().get().toString());
        }
    }
}
}
