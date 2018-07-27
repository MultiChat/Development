package xyz.olivermartin.multichat.bungee;

import java.io.Serializable;

public class TChatInfo
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private char chatcolor;
  private char namecolor;
  
  public char getChatColor()
  {
    return this.chatcolor;
  }
  
  public void setChatColor(char color)
  {
    this.chatcolor = color;
  }
  
  public char getNameColor()
  {
    return this.namecolor;
  }
  
  public void setNameColor(char color)
  {
    this.namecolor = color;
  }
}

