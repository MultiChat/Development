package com.olivermartin410.plugins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TGroupChatInfo
  extends TChatInfo
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private List<UUID> Members = new ArrayList<UUID>();
  private List<UUID> Viewers = new ArrayList<UUID>();
  private List<UUID> Admins = new ArrayList<UUID>();
  private List<UUID> BannedPlayers = new ArrayList<UUID>();
  private String PartyName;
  private boolean secret;
  private String password;
  private boolean formal;
  
  public void setPassword(String newpassword)
  {
    this.password = newpassword;
  }
  
  public void setFormal(boolean trueorfalse)
  {
    this.formal = trueorfalse;
  }
  
  public boolean getFormal()
  {
    return this.formal;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setSecret(boolean trueorfalse)
  {
    this.secret = trueorfalse;
  }
  
  public boolean getSecret()
  {
    return this.secret;
  }
  
  public String getName()
  {
    return this.PartyName;
  }
  
  public void setName(String Name)
  {
    this.PartyName = Name;
  }
  
  public List<UUID> getMembers()
  {
    return this.Members;
  }
  
  public void addMember(UUID memberuuid)
  {
    this.Members.add(memberuuid);
  }
  
  public void delMember(UUID memberuuid)
  {
    this.Members.remove(memberuuid);
  }
  
  public List<UUID> getAdmins()
  {
    return this.Admins;
  }
  
  public void addAdmin(UUID adminuuid)
  {
    this.Admins.add(adminuuid);
  }
  
  public void delAdmin(UUID adminuuid)
  {
    this.Admins.remove(adminuuid);
  }
  
  public boolean existsAdmin(UUID adminuuid)
  {
    if (this.Admins.contains(adminuuid)) {
      return true;
    }
    return false;
  }
  
  public List<UUID> getBanned()
  {
    return this.BannedPlayers;
  }
  
  public void addBanned(UUID banuuid)
  {
    this.BannedPlayers.add(banuuid);
  }
  
  public void delBanned(UUID banuuid)
  {
    this.BannedPlayers.remove(banuuid);
  }
  
  public boolean existsBanned(UUID banuuid)
  {
    if (this.BannedPlayers.contains(banuuid)) {
      return true;
    }
    return false;
  }
  
  public boolean existsMember(UUID memberuuid)
  {
    if (this.Members.contains(memberuuid)) {
      return true;
    }
    return false;
  }
  
  public List<UUID> getViewers()
  {
    return this.Viewers;
  }
  
  public void addViewer(UUID memberuuid)
  {
    this.Viewers.add(memberuuid);
  }
  
  public void delViewer(UUID memberuuid)
  {
    this.Viewers.remove(memberuuid);
  }
  
  public boolean existsViewer(UUID memberuuid)
  {
    if (this.Viewers.contains(memberuuid)) {
      return true;
    }
    return false;
  }
}

