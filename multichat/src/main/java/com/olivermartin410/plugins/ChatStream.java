package com.olivermartin410.plugins;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatStream {

	boolean whitelistMembers;
	protected List<UUID> members;
	
	boolean whitelistServers;
	protected List<String> servers;
	
	protected String name;
	protected String format;
	
	public ChatStream(String name,  String format, boolean whitelistServers, boolean whitelistMembers) {
		
		this.name = name;
		this.whitelistServers = whitelistServers;
		this.format = format;
		this.servers = new ArrayList<String>();
		this.members = new ArrayList<UUID>();
		this.whitelistMembers = whitelistMembers;
		
	}
	
	public void addServer(String server) {
		
		if (!servers.contains(server)) {
			servers.add(server);
		}
		
	}
	
	public void addMember(UUID member) {
	
		if (!members.contains(member)) {
			members.add(member);
		}
		
	}

	public String getName() {
		return this.name;
	}
	
	public String getFormat() {
		return this.format;
	}

	public void sendMessage(ProxiedPlayer sender, String message) {
		
		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
			if ( (whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {
				if ( (whitelistServers && servers.contains(receiver.getServer().getInfo().getName())) || (!whitelistServers && !servers.contains(receiver.getServer().getInfo().getName()))) {
					//TODO hiding & showing streams
					
					receiver.sendMessage(buildFormat(sender,receiver,format,message));
					
				}
			}
		}
		
	    System.out.println("\033[33m[MultiChat][CHAT]" + sender.getName() + ": " + message);
		
	}
	
	public void sendMessage(String senderToken, String message) {
		//TODO Build this
	}
	
	public BaseComponent[] buildFormat(ProxiedPlayer sender, ProxiedPlayer receiver, String format, String message) {

		String newFormat = format;
		
	    newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
	    newFormat = newFormat.replace("%NAME%", sender.getName());
	    newFormat = newFormat.replace("%DISPLAYNAMET%", receiver.getDisplayName());
	    newFormat = newFormat.replace("%NAMET%", receiver.getName());
	    newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
	    newFormat = newFormat.replace("%SERVERT%", receiver.getServer().getInfo().getName());
	    
	    String[] returnValues = fixFormatCodes(newFormat, "&f");
	    newFormat = returnValues[0] + "%MESSAGE%";
	    
	    String lastColour = returnValues[1];
	    BaseComponent[] toSend;
	    
	    if (sender.hasPermission("multichat.chat.colour") || sender.hasPermission("multichat.chat.color")) {
	    	newFormat = newFormat.replace("%MESSAGE%", fixFormatCodes(message, lastColour)[0]);
	    	String URLBIT = getURLBIT(message);
	    	toSend = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', newFormat)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create();
	    } else {
	    	
	    	ChatColor currentColor = null;
	    	switch (lastColour.toCharArray()[1]) {
			case '0':
				currentColor = ChatColor.BLACK;
				break;
			case '1':
				currentColor = ChatColor.DARK_BLUE;
				break;
			case '2':
				currentColor = ChatColor.DARK_GREEN;
				break;
			case '3':
				currentColor = ChatColor.DARK_AQUA;
				break;
			case '4':
				currentColor = ChatColor.DARK_RED;
				break;
			case '5':
				currentColor = ChatColor.DARK_PURPLE;
				break;
			case '6':
				currentColor = ChatColor.GOLD;
				break;
			case '7':
				currentColor = ChatColor.GRAY;
				break;
			case '8':
				currentColor = ChatColor.DARK_GRAY;
				break;
			case '9':
				currentColor = ChatColor.BLUE;
				break;
			case 'a':
				currentColor = ChatColor.GREEN;
				break;
			case 'b':
				currentColor = ChatColor.AQUA;
				break;
			case 'c':
				currentColor = ChatColor.RED;
				break;
			case 'd':
				currentColor = ChatColor.LIGHT_PURPLE;
				break;
			case 'e':
				currentColor = ChatColor.YELLOW;
				break;
			case 'f':
				currentColor = ChatColor.WHITE;
				break;
			case 'r':
				currentColor = ChatColor.RESET;
				break;
			}
	    	
	    	boolean bold = false;
	    	boolean italic = false;
	    	boolean magic = false;
	    	boolean underline = false;
	    	boolean strike = false;
	    	
	    	if (lastColour.contains("&l")) bold = true;
	    	if (lastColour.contains("&o")) italic = true;
	    	if (lastColour.contains("&m")) strike = true;
	    	if (lastColour.contains("&n")) underline = true;
	    	if (lastColour.contains("&k")) magic = true;
	    	
	    	newFormat = newFormat.replace("%MESSAGE%", "");
	    	
		    String URLBIT = getURLBIT(message);
	    	
	    	toSend = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', newFormat)).append(message).color(currentColor).bold(bold).italic(italic).underlined(underline).strikethrough(strike).obfuscated(magic).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create();
	    }
		
		return toSend;
		
	}
	
    /*public List<String> getURLs (String s) {
    	
    	List<String> urls = new ArrayList<String>();

	        // separate input by spaces ( URLs don't have spaces )
	        String [] parts = s.split("\\s+");

	        // Attempt to convert each item into an URL.   
	        for( String item : parts ) try {
	        	while (item.startsWith("&")) {
	        		String temp;
	        		try {
	        			temp = item.substring(2);
	        			item = temp;
	        		} catch (IndexOutOfBoundsException e) {
	        			
	        		}
	        	}
	            URL url = new URL(item);
	            
	            urls.add(item);
	            urls.add(Integer.toString(s.indexOf(url.toString())));
	            urls.add(Integer.toString(s.indexOf(url.toString()) + url.toString().length() - 1));
	            
	        } catch (MalformedURLException e) { }
	        
	        return urls;

    }*/
	
	public String getURLBIT(String Message)
	  {
	    String URLBIT = "";
	    if ((Message.toLowerCase().contains("http://")) || (Message.toLowerCase().contains("https://")) || (Message.toLowerCase().contains("www.")) || (Message.toLowerCase().contains(".com") ) || (Message.toLowerCase().contains(".uk") ) || (Message.toLowerCase().contains(".us") ) || (Message.toLowerCase().contains(".net") ) || (Message.toLowerCase().contains(".org") ))
	    {
	      String[] splited = Message.split("\\s+");
	      for (String word : splited) {
	        if ((word.toLowerCase().contains("http://")) || (word.toLowerCase().contains("https://")) || (word.toLowerCase().contains("www.")) || (word.toLowerCase().contains(".com")) || (word.toLowerCase().contains(".uk")) || (word.toLowerCase().contains(".us")) || (word.toLowerCase().contains(".net")) || (word.toLowerCase().contains(".org"))) {
	          URLBIT = word;
	        }
	      }
	    }
	    if (!URLBIT.equals("")) {
	      while (URLBIT.toCharArray()[0] == '&') {
	        URLBIT = URLBIT.substring(2);
	      }
	    }
	    return URLBIT;
	  }
	
    /*public BaseComponent[] buildMessage(String message) {
    	return buildMessage(message, true, -1);
    }
   
	public BaseComponent[] buildMessage(String message, boolean colour, int formatLength) {
		if (colour) {
			formatLength = -1;
		}
		List<String> urls = getURLs(message);
		
		String urlStart = "0";
		String urlEnd = "0";
		String url = "";
		int counter = 0;
		
		if (urls.size() != 0) {
			urlStart = urls.get(0);
			urlEnd = urls.get(1);
			url = urls.get(2);
			counter = 3;
		}
		
		ComponentBuilder messageParts = new ComponentBuilder("");

		ChatColor currentColor = ChatColor.WHITE;
		
		for (int i = 0; i < message.length(); i++) {
			
			if (message.toCharArray()[i] == '&' && (i < formatLength - 1 || formatLength == -1) ) {
				
				if (i < message.length() - 1) {
				
				switch (message.toCharArray()[i+1]) {
				case '0':
					currentColor = ChatColor.BLACK;
					break;
				case '1':
					currentColor = ChatColor.DARK_BLUE;
					break;
				case '2':
					currentColor = ChatColor.DARK_GREEN;
					break;
				case '3':
					currentColor = ChatColor.DARK_AQUA;
					break;
				case '4':
					currentColor = ChatColor.DARK_RED;
					break;
				case '5':
					currentColor = ChatColor.DARK_PURPLE;
					break;
				case '6':
					currentColor = ChatColor.GOLD;
					break;
				case '7':
					currentColor = ChatColor.GRAY;
					break;
				case '8':
					currentColor = ChatColor.DARK_GRAY;
					break;
				case '9':
					currentColor = ChatColor.BLUE;
					break;
				case 'a':
					currentColor = ChatColor.GREEN;
					break;
				case 'b':
					currentColor = ChatColor.AQUA;
					break;
				case 'c':
					currentColor = ChatColor.RED;
					break;
				case 'd':
					currentColor = ChatColor.LIGHT_PURPLE;
					break;
				case 'e':
					currentColor = ChatColor.YELLOW;
					break;
				case 'f':
					currentColor = ChatColor.WHITE;
					break;
				case 'r':
					currentColor = ChatColor.RESET;
					break;
				case 'k':
					currentColor = ChatColor.MAGIC;
					break;
				case 'l':
					currentColor = ChatColor.BOLD;
					break;
				case 'm':
					currentColor = ChatColor.STRIKETHROUGH;
					break;
				case 'n':
					currentColor = ChatColor.UNDERLINE;
					break;
				case 'o':
					currentColor = ChatColor.ITALIC;
					break;
				default:
					messageParts.append("&" + message.toCharArray()[i+1]).color(currentColor);
				}
				
				} else {
					messageParts.append("&").color(currentColor);
				}
				
				i++;
			} else {
				String link = "";
				if (i >= Integer.parseInt(urlStart) && i <= Integer.parseInt(urlEnd)) {
					link = url;
				} else if (i > Integer.parseInt(urlEnd) && counter < urls.size()) {
					urlStart = urls.get(counter);
					urlEnd = urls.get(counter+1);
					url = urls.get(counter+2);
					counter = counter + 3;
				}
				messageParts.append(message.substring(i, i)).color(currentColor).event(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
			}
			
		}

		return messageParts.create();
	}*/
	
	public static String[] fixFormatCodes(String OriginalMessage, String startColour)
	  {
	    String colour = startColour;
	    char previouschar = ' ';
	    String EditMessage = OriginalMessage.replaceAll("§", "&");
	    OriginalMessage = "";
	    for (int i = 0; i <= EditMessage.length() - 1; i++) {
	      if (i != EditMessage.length() - 1)
	      {
	        if (EditMessage.toCharArray()[i] == '&')
	        {
	          if ((EditMessage.toCharArray()[(i + 1)] == 'r') || (EditMessage.toCharArray()[(i + 1)] == 'k') || (EditMessage.toCharArray()[(i + 1)] == 'o') || (EditMessage.toCharArray()[(i + 1)] == 'n') || (EditMessage.toCharArray()[(i + 1)] == 'm') || (EditMessage.toCharArray()[(i + 1)] == 'l') || (EditMessage.toCharArray()[(i + 1)] == 'a') || (EditMessage.toCharArray()[(i + 1)] == 'b') || (EditMessage.toCharArray()[(i + 1)] == 'c') || (EditMessage.toCharArray()[(i + 1)] == 'd') || (EditMessage.toCharArray()[(i + 1)] == 'e') || (EditMessage.toCharArray()[(i + 1)] == 'f') || (EditMessage.toCharArray()[(i + 1)] == '0') || (EditMessage.toCharArray()[(i + 1)] == '1') || (EditMessage.toCharArray()[(i + 1)] == '2') || (EditMessage.toCharArray()[(i + 1)] == '3') || (EditMessage.toCharArray()[(i + 1)] == '4') || (EditMessage.toCharArray()[(i + 1)] == '5') || (EditMessage.toCharArray()[(i + 1)] == '6') || (EditMessage.toCharArray()[(i + 1)] == '7') || (EditMessage.toCharArray()[(i + 1)] == '8') || (EditMessage.toCharArray()[(i + 1)] == '9')) {
	            if (i >= 2)
	            {
	              if (EditMessage.toCharArray()[(i - 2)] != '&') {
	                colour = "&" + EditMessage.toCharArray()[(i + 1)];
	              } else if ((EditMessage.toCharArray()[(i + 1)] == 'r') || (EditMessage.toCharArray()[(i + 1)] == 'k') || (EditMessage.toCharArray()[(i + 1)] == 'o') || (EditMessage.toCharArray()[(i + 1)] == 'n') || (EditMessage.toCharArray()[(i + 1)] == 'm') || (EditMessage.toCharArray()[(i + 1)] == 'l') || (EditMessage.toCharArray()[(i - 1)] == 'a') || (EditMessage.toCharArray()[(i - 1)] == 'b') || (EditMessage.toCharArray()[(i - 1)] == 'c') || (EditMessage.toCharArray()[(i - 1)] == 'd') || (EditMessage.toCharArray()[(i - 1)] == 'e') || (EditMessage.toCharArray()[(i - 1)] == 'f') || (EditMessage.toCharArray()[(i - 1)] == '0') || (EditMessage.toCharArray()[(i - 1)] == '1') || (EditMessage.toCharArray()[(i - 1)] == '2') || (EditMessage.toCharArray()[(i - 1)] == '3') || (EditMessage.toCharArray()[(i - 1)] == '4') || (EditMessage.toCharArray()[(i - 1)] == '5') || (EditMessage.toCharArray()[(i - 1)] == '6') || (EditMessage.toCharArray()[(i - 1)] == '7') || (EditMessage.toCharArray()[(i - 1)] == '8') || (EditMessage.toCharArray()[(i - 1)] == '9')) {
	                colour = colour + "&" + EditMessage.toCharArray()[(i + 1)];
	              } else {
	                colour = "&" + EditMessage.toCharArray()[(i + 1)];
	              }
	            }
	            else {
	              colour = "&" + EditMessage.toCharArray()[(i + 1)];
	            }
	          }
	          OriginalMessage = OriginalMessage + EditMessage.toCharArray()[i];

	        }
	        else if (EditMessage.toCharArray()[i] != '&' && previouschar != '&' )
	        {
	          OriginalMessage = OriginalMessage + EditMessage.toCharArray()[i] + colour;
	        }
	        else
	        {
	          OriginalMessage = OriginalMessage + EditMessage.toCharArray()[i];
	        }
	      }
	      else {
	        OriginalMessage = OriginalMessage + EditMessage.toCharArray()[i];
	      }
	      previouschar = EditMessage.toCharArray()[i];
	    }
	    
	    String[] returnValues = new String[2];
	    returnValues[0] = OriginalMessage;
	    returnValues[1] = colour;
	    
	    return returnValues;
	  }
}
