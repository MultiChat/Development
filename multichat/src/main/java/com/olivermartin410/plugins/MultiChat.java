package com.olivermartin410.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class MultiChat extends Plugin implements Listener {
	
	  public static String latestversion = "1.3.4";
	  public static Map<UUID, TChatInfo> modchatpreferences = new HashMap<UUID, TChatInfo>();
	  public static Map<UUID, TChatInfo> adminchatpreferences = new HashMap<UUID, TChatInfo>();
	  public static Map<String, TGroupChatInfo> groupchats = new HashMap<String, TGroupChatInfo>();
	  public static Map<UUID, String> viewedchats = new HashMap<UUID, String>();
	  public static List<UUID> allspy = new ArrayList<UUID>();
	  public static List<UUID> socialspy = new ArrayList<UUID>();
	  public static File ConfigDir;
	  public static String configversion;
	  public static ConfigManager configman = new ConfigManager();
	  public static JMConfigManager jmconfigman = new JMConfigManager();
	  public static Map<UUID, Boolean> globalplayers = new HashMap<UUID, Boolean>();
	  public static Map<UUID, UUID> lastmsg = new HashMap<UUID, UUID>();
	  public static boolean frozen;
	  
	  private static MultiChat instance;
	  
	  public static MultiChat getInstance() {
		    return instance;
		}
	  
	  public void backup()
	  {
	    getProxy().getScheduler().schedule(this, new Runnable()
	    {
	      public void run()
	      {
	        getLogger().info("Commencing backup!");
	        saveChatInfo();
	        saveGroupChatInfo();
	        saveGroupSpyInfo();
	        saveGlobalChatInfo();
	        saveSocialSpyInfo();
	        saveAnnouncements();
	        saveBulletins();
	        UUIDNameManager.saveUUIDS();
	      }
	    }, 1L, 60L, TimeUnit.MINUTES);
	  }
	  
	  public void fetchdisplaynames()
	  {
	    getProxy().getScheduler().schedule(this, new Runnable()
	    {
	      public void run()
	      {
	        if (configman.config.getBoolean("fetch_spigot_display_names") == true)
	        {
	          getLogger().info("Fetching display names!");
	          getProxy();
	          for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
	            BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
	          }
	        }
	      }
	    }, 1L, 5L, TimeUnit.MINUTES);
	  }
	  
	  @EventHandler
	  public void onLogin(PostLoginEvent event)
	  {
	    fetchdisplaynameonce(event.getPlayer().getName());
	  }
	  
	  @EventHandler
	  public void onServerSwitch(ServerSwitchEvent event)
	  {
	    fetchdisplaynameonce(event.getPlayer().getName());
	  }
	  
	  public void fetchdisplaynameonce(final String playername)
	  {
	    getProxy().getScheduler().schedule(this, new Runnable()
	    {
	      public void run()
	      {
	        try
	        {
	          if (configman.config.getBoolean("fetch_spigot_display_names") == true)
	          {
	            ProxiedPlayer player = getProxy().getPlayer(playername);
	            BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
	          }
	        }
	        catch (NullPointerException ex) {}
	      }
	    }, 0L, TimeUnit.SECONDS);
	    
	    getProxy().getScheduler().schedule(this, new Runnable()
	    {
	      public void run()
	      {
	        try
	        {
	          if (configman.config.getBoolean("fetch_spigot_display_names") == true)
	          {
	            ProxiedPlayer player = getProxy().getPlayer(playername);
	            BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
	          }
	        }
	        catch (NullPointerException ex) {}
	      }
	    }, 1L, TimeUnit.SECONDS);
	    
	    getProxy().getScheduler().schedule(this, new Runnable()
	    {
	      public void run()
	      {
	        try
	        {
	          if (configman.config.getBoolean("fetch_spigot_display_names") == true)
	          {
	            ProxiedPlayer player = getProxy().getPlayer(playername);
	            BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
	          }
	        }
	        catch (NullPointerException ex) {}
	      }
	    }, 2L, TimeUnit.SECONDS);
	    
	    getProxy().getScheduler().schedule(this, new Runnable()
	    {
	      public void run()
	      {
	        try
	        {
	          if (configman.config.getBoolean("fetch_spigot_display_names") == true)
	          {
	            ProxiedPlayer player = getProxy().getPlayer(playername);
	            BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
	          }
	        }
	        catch (NullPointerException ex)
	        {
	          System.out.println("[MultiChat] Could not fetch display name for player");
	        }
	      }
	    }, 4L, TimeUnit.SECONDS);
	  }
	  
	  public void onEnable()
	  {
		  instance = this;
	    ConfigDir = getDataFolder();
	    if (!getDataFolder().exists())
	    {
	      System.out.println("[MultiChat] Creating plugin directory!");
	      getDataFolder().mkdirs();
	    }
	    configman.startupConfig();
	    jmconfigman.startupConfig();
	    configversion = configman.config.getString("version");
	    if (configversion.equals(latestversion) || configversion.equals("1.3")  || configversion.equals("1.3.1")  || configversion.equals("1.3.2") || configversion.equals("1.3.3"))
	    {
	      getProxy().getPluginManager().registerListener(this, new Events());
	      getProxy().getPluginManager().registerListener(this, this);
	      getProxy().getPluginManager().registerCommand(this, new MCCommand());
	      getProxy().getPluginManager().registerCommand(this, new ACCommand());
	      getProxy().getPluginManager().registerCommand(this, new MCCCommand());
	      getProxy().getPluginManager().registerCommand(this, new ACCCommand());
	      getProxy().getPluginManager().registerCommand(this, new GCCommand());
	      getProxy().getPluginManager().registerCommand(this, new GroupCommand());
	      getProxy().getPluginManager().registerCommand(this, new StaffListCommand());
	      getProxy().getPluginManager().registerCommand(this, new GroupListCommand());
	      getProxy().getPluginManager().registerCommand(this, new MultiChatCommand());
	      getProxy().getPluginManager().registerCommand(this, new DisplayCommand());
	      getProxy().getPluginManager().registerCommand(this, new LocalCommand());
	      getProxy().getPluginManager().registerCommand(this, new GlobalCommand());
	      getProxy().getPluginManager().registerCommand(this, new FreezeChatCommand());
	      getProxy().getPluginManager().registerCommand(this, new HelpMeCommand());
	      getProxy().getPluginManager().registerCommand(this, new ClearChatCommand());
	      getProxy().getPluginManager().registerCommand(this, new AnnouncementCommand());
	      getProxy().getPluginManager().registerCommand(this, new BulletinCommand());
	      getProxy().registerChannel("MultiChat");
	      getProxy().getPluginManager().registerListener(this, new BungeeComm());
	      if (configman.config.getBoolean("pm"))
	      {
	        getProxy().getPluginManager().registerCommand(this, new MsgCommand());
	        getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
	        getProxy().getPluginManager().registerCommand(this, new SocialSpyCommand());
	      }
	      System.out.println("[MultiChat] VERSION LOADED: " + configversion);
	      
	      Startup();
	      UUIDNameManager.Startup();
	      
	      getLogger().info("MultiChat has been initiated!");
	      
	      backup();
	      fetchdisplaynames();
	    }
	    else
	    {
	      getLogger().info("Config incorrect version! Please repair or delete it!");
	    }
	  }
	  
	  public void onDisable()
	  {
	    getLogger().info("Thankyou for using MultiChat. Disabling...");
	    
	    saveChatInfo();
	    saveGroupChatInfo();
	    saveGroupSpyInfo();
	    saveGlobalChatInfo();
	    saveSocialSpyInfo();
	    saveAnnouncements();
	    saveBulletins();
	    UUIDNameManager.saveUUIDS();
	  }
	  
	  public static void saveAnnouncements()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "Announcements.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(Announcements.getAnnouncementList());
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The announcements file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the announcements file!");
	      e.printStackTrace();
	    }
	  }
	  
	  public static void saveBulletins()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "Bulletins.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeBoolean(Bulletins.isEnabled());
	      out.writeInt(Bulletins.getTimeBetween());
	      out.writeObject(Bulletins.getArrayList());
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The bulletins file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the bulletins file!");
	      e.printStackTrace();
	    }
	  }
	  
	  public static void saveChatInfo()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "StaffChatInfo.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(modchatpreferences);
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The mod chat info file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the mod chat info file!");
	      e.printStackTrace();
	    }
	    try
	    {
	      File file = new File(ConfigDir, "AdminChatInfo.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(adminchatpreferences);
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The admin chat info file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the admin chat info file!");
	      e.printStackTrace();
	    }
	  }
	  
	  public static void saveGroupChatInfo()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "GroupChatInfo.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(groupchats);
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The group chat info file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the group chat info file!");
	      e.printStackTrace();
	    }
	  }
	  
	  public static void saveGroupSpyInfo()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "GroupSpyInfo.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(allspy);
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The group spy info file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the group spy info file!");
	      e.printStackTrace();
	    }
	  }
	  
	  public static void saveSocialSpyInfo()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "SocialSpyInfo.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(socialspy);
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The social spy info file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the social spy info file!");
	      e.printStackTrace();
	    }
	  }
	  
	  public static void saveGlobalChatInfo()
	  {
	    try
	    {
	      File file = new File(ConfigDir, "GlobalChatInfo.dat");
	      FileOutputStream saveFile = new FileOutputStream(file);
	      ObjectOutputStream out = new ObjectOutputStream(saveFile);
	      out.writeObject(globalplayers);
	      out.close();
	      System.out.println("[MultiChat] SAVE ROUTINE: The global chat info file was successfully saved!");
	    }
	    catch (IOException e)
	    {
	      System.out.println("[MultiChat] SAVE ROUTINE:  An error has occured writing the global chat info file!");
	      e.printStackTrace();
	    }
	  }
	  
	  @SuppressWarnings("unchecked")
	public static HashMap<UUID, TChatInfo> loadModChatInfo()
	  {
	    HashMap<UUID, TChatInfo> result = null;
	    try
	    {
	      File file = new File(ConfigDir, "StaffChatInfo.dat");
	      FileInputStream saveFile = new FileInputStream(file);
	      ObjectInputStream in = new ObjectInputStream(saveFile);
	      result = (HashMap<UUID, TChatInfo>)in.readObject();
	      in.close();
	      System.out.println("[MultiChat] LOAD ROUTINE: The mod chat info file was successfully loaded!");
	    }
	    catch (IOException|ClassNotFoundException e)
	    {
	      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the mod chat info file!");
	      e.printStackTrace();
	    }
	    return result;
	  }
	  
	  @SuppressWarnings("unchecked")
		public static void loadBulletins()
		  {
		   ArrayList<String> result = null;
		   boolean enabled = false;
		   int timeBetween = 0;
		    try
		    {
		      File file = new File(ConfigDir, "Bulletins.dat");
		      FileInputStream saveFile = new FileInputStream(file);
		      ObjectInputStream in = new ObjectInputStream(saveFile);
		      enabled = in.readBoolean();
		      timeBetween = in.readInt();
		      result = (ArrayList<String>)in.readObject();
		      in.close();
		      Bulletins.setArrayList(result);
		      if (enabled) {
		    	  Bulletins.startBulletins(timeBetween);
		      }
		      System.out.println("[MultiChat] LOAD ROUTINE: The bulletins file was successfully loaded!");
		    }
		    catch (IOException|ClassNotFoundException e)
		    {
		      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the bulletins file!");
		      e.printStackTrace();
		    }
		  }
	  
	  @SuppressWarnings("unchecked")
		public static HashMap<String, String> loadAnnouncements()
		  {
		    HashMap<String, String> result = null;
		    try
		    {
		      File file = new File(ConfigDir, "Announcements.dat");
		      FileInputStream saveFile = new FileInputStream(file);
		      ObjectInputStream in = new ObjectInputStream(saveFile);
		      result = (HashMap<String, String>)in.readObject();
		      in.close();
		      System.out.println("[MultiChat] LOAD ROUTINE: The announcements file was successfully loaded!");
		    }
		    catch (IOException|ClassNotFoundException e)
		    {
		      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the announcements file!");
		      e.printStackTrace();
		    }
		    return result;
		  }
	  
	  @SuppressWarnings("unchecked")
	public static HashMap<UUID, TChatInfo> loadAdminChatInfo()
	  {
	    HashMap<UUID, TChatInfo> result = null;
	    try
	    {
	      File file = new File(ConfigDir, "AdminChatInfo.dat");
	      FileInputStream saveFile = new FileInputStream(file);
	      ObjectInputStream in = new ObjectInputStream(saveFile);
	      result = (HashMap<UUID, TChatInfo>)in.readObject();
	      in.close();
	      System.out.println("[MultiChat] LOAD ROUTINE: The admin chat info file was successfully loaded!");
	    }
	    catch (IOException|ClassNotFoundException e)
	    {
	      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the admin chat info file!");
	      e.printStackTrace();
	    }
	    return result;
	  }
	  
	  @SuppressWarnings("unchecked")
	public static HashMap<String, TGroupChatInfo> loadGroupChatInfo()
	  {
	    HashMap<String, TGroupChatInfo> result = null;
	    try
	    {
	      File file = new File(ConfigDir, "GroupChatInfo.dat");
	      FileInputStream saveFile = new FileInputStream(file);
	      ObjectInputStream in = new ObjectInputStream(saveFile);
	      result = (HashMap<String, TGroupChatInfo>)in.readObject();
	      in.close();
	      System.out.println("[MultiChat] LOAD ROUTINE: The group chat info file was successfully loaded!");
	    }
	    catch (IOException|ClassNotFoundException e)
	    {
	      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the group chat info file!");
	      e.printStackTrace();
	    }
	    return result;
	  }
	  
	  @SuppressWarnings("unchecked")
	public static List<UUID> loadGroupSpyInfo()
	  {
	    List<UUID> result = null;
	    try
	    {
	      File file = new File(ConfigDir, "GroupSpyInfo.dat");
	      FileInputStream saveFile = new FileInputStream(file);
	      ObjectInputStream in = new ObjectInputStream(saveFile);
	      result = (List<UUID>)in.readObject();
	      in.close();
	      System.out.println("[MultiChat] LOAD ROUTINE: The group spy info file was successfully loaded!");
	    }
	    catch (IOException|ClassNotFoundException e)
	    {
	      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the group spy info file!");
	      e.printStackTrace();
	    }
	    return result;
	  }
	  
	  @SuppressWarnings("unchecked")
	public static List<UUID> loadSocialSpyInfo()
	  {
	    List<UUID> result = null;
	    try
	    {
	      File file = new File(ConfigDir, "SocialSpyInfo.dat");
	      FileInputStream saveFile = new FileInputStream(file);
	      ObjectInputStream in = new ObjectInputStream(saveFile);
	      result = (List<UUID>)in.readObject();
	      in.close();
	      System.out.println("[MultiChat] LOAD ROUTINE: The social spy info file was successfully loaded!");
	    }
	    catch (IOException|ClassNotFoundException e)
	    {
	      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the social spy info file!");
	      e.printStackTrace();
	    }
	    return result;
	  }
	  
	  @SuppressWarnings("unchecked")
	public static Map<UUID, Boolean> loadGlobalChatInfo()
	  {
	    Map<UUID, Boolean> result = null;
	    try
	    {
	      File file = new File(ConfigDir, "GlobalChatInfo.dat");
	      FileInputStream saveFile = new FileInputStream(file);
	      ObjectInputStream in = new ObjectInputStream(saveFile);
	      result = (Map<UUID, Boolean>)in.readObject();
	      in.close();
	      System.out.println("[MultiChat] LOAD ROUTINE: The global chat info file was successfully loaded!");
	    }
	    catch (IOException|ClassNotFoundException e)
	    {
	      System.out.println("[MultiChat] LOAD ROUTINE: An error has occured reading the global chat info file!");
	      e.printStackTrace();
	    }
	    return result;
	  }
	  
	  public static void Startup()
	  {
	    File f = new File(ConfigDir, "StaffChatInfo.dat");
	    File f2 = new File(ConfigDir, "AdminChatInfo.dat");
	    if ((f.exists()) && (!f.isDirectory()) && (f2.exists()) && (!f2.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for StaffChat");
	      modchatpreferences.putAll(loadModChatInfo());
	      adminchatpreferences.putAll(loadAdminChatInfo());
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some staff chat files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Welcome to MultiChat! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveChatInfo();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    File f3 = new File(ConfigDir, "GroupChatInfo.dat");
	    if ((f3.exists()) && (!f3.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for GroupChat");
	      groupchats.putAll(loadGroupChatInfo());
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some group chat files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Enabling Group Chats! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveGroupChatInfo();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    File f4 = new File(ConfigDir, "GroupSpyInfo.dat");
	    if ((f4.exists()) && (!f4.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for Group Spy");
	      allspy = loadGroupSpyInfo();
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some group spy files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Enabling Group-Spy! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveGroupSpyInfo();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    File f5 = new File(ConfigDir, "GlobalChatInfo.dat");
	    if ((f5.exists()) && (!f5.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for Global Chat Info");
	      globalplayers = loadGlobalChatInfo();
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some global chat files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Enabling Global Chat! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveGlobalChatInfo();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    File f6 = new File(ConfigDir, "SocialSpyInfo.dat");
	    if ((f6.exists()) && (!f6.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for Social Spy");
	      socialspy = loadSocialSpyInfo();
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some social spy files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Enabling Social Spy! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveGroupSpyInfo();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    File f7 = new File(ConfigDir, "Announcements.dat");
	    if ((f7.exists()) && (!f7.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for Announcements");
	      Announcements.loadAnnouncementList((loadAnnouncements()));
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some announcements files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Welcome to MultiChat! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveAnnouncements();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    System.out.println("[MultiChat] [COMPLETE] Load sequence successful!");
	    File f8 = new File(ConfigDir, "Bulletins.dat");
	    if ((f8.exists()) && (!f8.isDirectory()))
	    {
	      System.out.println("[MultiChat] Attempting startup load for Bulletins");
	      loadBulletins();
	      System.out.println("[MultiChat] Load completed!");
	    }
	    else
	    {
	      System.out.println("[MultiChat] Some bulletins files do not exist to load. Must be first startup!");
	      System.out.println("[MultiChat] Welcome to MultiChat! :D");
	      System.out.println("[MultiChat] Attempting to create hash files!");
	      saveBulletins();
	      System.out.println("[MultiChat] The files were created!");
	    }
	    System.out.println("[MultiChat] [COMPLETE] Load sequence successful!");
	  }

}
