package com.olivermartin410.plugins;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatManipulation
{
  public String FixFormatCodes(String OriginalMessage)
  {
    String colour = "&f";
    String EditMessage = OriginalMessage;
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
        else if (EditMessage.toCharArray()[i] == ' ')
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
    }
    return OriginalMessage;
  }
  
  public String getURLBIT(String Message)
  {
    String URLBIT = "";
    if ((Message.toLowerCase().contains("http://")) || (Message.toLowerCase().contains("https://")) || (Message.toLowerCase().contains("www.")) || (Message.toLowerCase().contains(".com")))
    {
      String[] splited = Message.split("\\s+");
      for (String word : splited) {
        if ((word.toLowerCase().contains("http://")) || (word.toLowerCase().contains("https://")) || (word.toLowerCase().contains("www.")) || (word.toLowerCase().contains(".com"))) {
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
  
  public String replaceMsgVars(String MessageFormat, String Message, ProxiedPlayer sender, ProxiedPlayer target)
  {
    MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
    MessageFormat = MessageFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
    MessageFormat = MessageFormat.replace("%NAME%", sender.getName());
    MessageFormat = MessageFormat.replace("%DISPLAYNAMET%", target.getDisplayName());
    MessageFormat = MessageFormat.replace("%NAMET%", target.getName());
    MessageFormat = MessageFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
    MessageFormat = MessageFormat.replace("%SERVERT%", target.getServer().getInfo().getName());
    
    return MessageFormat;
  }
  
  public String replaceChatVars(String MessageFormat, ProxiedPlayer sender)
  {
    MessageFormat = MessageFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
    MessageFormat = MessageFormat.replace("%NAME%", sender.getName());
    MessageFormat = MessageFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
    
    return MessageFormat;
  }
  
  public String replaceModChatVars(String MessageFormat, String playername, String displayname, String server, String Message, ProxiedPlayer target)
  {
    MessageFormat = MessageFormat.replace("%DISPLAYNAME%", displayname);
    MessageFormat = MessageFormat.replace("%NAME%", playername);
    MessageFormat = MessageFormat.replace("%SERVER%", server);
    MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
    MessageFormat = MessageFormat.replace("%CC%", "&" + ((TChatInfo)MultiChat.modchatpreferences.get(target.getUniqueId())).getChatColor());
    MessageFormat = MessageFormat.replace("%NC%", "&" + ((TChatInfo)MultiChat.modchatpreferences.get(target.getUniqueId())).getNameColor());
    
    return MessageFormat;
  }
  
  public String replaceAdminChatVars(String MessageFormat, String playername, String displayname, String server, String Message, ProxiedPlayer target)
  {
    MessageFormat = MessageFormat.replace("%DISPLAYNAME%",displayname);
    MessageFormat = MessageFormat.replace("%NAME%", playername);
    MessageFormat = MessageFormat.replace("%SERVER%", server);
    MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
    MessageFormat = MessageFormat.replace("%CC%", "&" + ((TChatInfo)MultiChat.adminchatpreferences.get(target.getUniqueId())).getChatColor());
    MessageFormat = MessageFormat.replace("%NC%", "&" + ((TChatInfo)MultiChat.adminchatpreferences.get(target.getUniqueId())).getNameColor());
    
    return MessageFormat;
  }
  
  public String replaceGroupChatVars(String MessageFormat, String sendername, String Message, String GroupName)
  {
    MessageFormat = MessageFormat.replace("%NAME%", sendername);
    MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
    MessageFormat = MessageFormat.replace("%CC%", "&" + ((TGroupChatInfo)MultiChat.groupchats.get(GroupName)).getChatColor());
    MessageFormat = MessageFormat.replace("%NC%", "&" + ((TGroupChatInfo)MultiChat.groupchats.get(GroupName)).getNameColor());
    MessageFormat = MessageFormat.replace("%GROUPNAME%", GroupName.toUpperCase());
    
    return MessageFormat;
  }
  
  public String replaceJoinMsgVars(String MessageFormat, String sendername)
  {
    MessageFormat = MessageFormat.replace("%NAME%", sendername);

    return MessageFormat;
  }
}
