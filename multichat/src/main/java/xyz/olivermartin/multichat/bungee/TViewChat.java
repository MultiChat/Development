package xyz.olivermartin.multichat.bungee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TViewChat
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private List<String> viewedchats = new ArrayList<String>();
  private String selectedchat = new String();
  
  public List<String> getViewed()
  {
    return this.viewedchats;
  }
  
  public void addViewed(String chat)
  {
    this.viewedchats.add(chat);
  }
  
  public boolean isViewing(String chat)
  {
    if (this.viewedchats.contains(chat)) {
      return true;
    }
    return false;
  }
  
  public void delViewed(String chat)
  {
    this.viewedchats.remove(chat);
  }
  
  public String getSelected()
  {
    return this.selectedchat;
  }
  
  public void setSelected(String chat)
  {
    this.selectedchat = chat;
  }
}

