package xyz.olivermartin.multichat.local.spigot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.common.database.DatabaseMode;
import xyz.olivermartin.multichat.local.LocalConfigManager;
import xyz.olivermartin.multichat.local.LocalDataStore;
import xyz.olivermartin.multichat.local.LocalDatabaseCredentials;
import xyz.olivermartin.multichat.local.LocalFileNameManager;
import xyz.olivermartin.multichat.local.LocalMetaManager;
import xyz.olivermartin.multichat.local.LocalNameManager;
import xyz.olivermartin.multichat.local.LocalNameManagerMode;
import xyz.olivermartin.multichat.local.LocalSQLNameManager;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.communication.LocalProxyCommunicationManager;
import xyz.olivermartin.multichat.spigotbridge.listeners.MultiChatPluginMessageListener;

public class MultiChatLocalSpigotPlugin extends JavaPlugin {

	@Override
	public void onEnable() {

		MultiChatLocal api = MultiChatLocal.getInstance();

		MultiChatLocalPlatform platform = MultiChatLocalPlatform.SPIGOT;
		api.registerPlatform(MultiChatLocalPlatform.SPIGOT);

		String pluginName = "MultiChatSpigot";
		api.registerPluginName(pluginName);

		File configDir = getDataFolder().getAbsoluteFile();
		if (!getDataFolder().exists()) {
			//TODO Need to use logger properly! getLogger().info(logPrefix + "Creating plugin directory!");
			getDataFolder().mkdirs();
			configDir = getDataFolder();
		}
		api.registerConfigDirectory(configDir);

		String translationsDirString = configDir.toString() + File.separator + "translations";
		File translationsDir = new File(translationsDirString);
		if (!translationsDir.exists()) {
			//TODO logger getLogger().info(logPrefix + "Creating translations directory!");
			translationsDir.mkdirs();
		}

		LocalConfigManager configMan = new LocalConfigManager();
		api.registerConfigManager(configMan);

		configMan.registerLocalConfig(platform, "spigotconfig.yml", configDir);
		configMan.registerLocalConfig(platform, "spigotconfig_fr.yml", translationsDir);

		LocalDataStore dataStore = new LocalDataStore();
		api.registerDataStore(dataStore);

		LocalNameManager nameManager;

		// TODO Move setup database to somewhere else? --> LocalDatabaseManager can definitely do this.
		if (configMan.getLocalConfig().isNicknameSQL()) {

			try {

				if (configMan.getLocalConfig().isMySQL()) {

					// MYSQL SETTINGS

					DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);

					DatabaseManager.getInstance().setURLMySQL(LocalDatabaseCredentials.getInstance().getURL());
					DatabaseManager.getInstance().setUsernameMySQL(LocalDatabaseCredentials.getInstance().getUser());
					DatabaseManager.getInstance().setPasswordMySQL(LocalDatabaseCredentials.getInstance().getPassword());

					DatabaseManager.getInstance().createDatabase("multichatspigot.db", LocalDatabaseCredentials.getInstance().getDatabase());

				} else {

					// SQLITE SETTINGS

					DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);
					DatabaseManager.getInstance().setPathSQLite(configDir);

					DatabaseManager.getInstance().createDatabase("multichatspigot.db");

				}

				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().connectToDatabase();
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().safeUpdate("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().safeUpdate("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");

				nameManager = new LocalSQLNameManager("multichatspigot.db");

			} catch (SQLException e) {

				// TODO Show error?!
				nameManager = new LocalSpigotFileNameManager();

			}

		} else {

			nameManager = new LocalSpigotFileNameManager();

		}

		if (nameManager.getMode() == LocalNameManagerMode.FILE) {

			LocalFileNameManager fileNameManager = (LocalFileNameManager)nameManager;

			// TODO This here is the legacy file loading code... This needs to be refactored to somewhere else!

			/* START LEGACY */

			String nameDataFile = "namedata.dat";
			File legacyNicknameFile = new File(configDir,"Nicknames.dat");
			String logPrefix = "[MultiChatSpigot] ";

			File f = new File(configDir, nameDataFile);

			if ((f.exists()) && (!f.isDirectory())) {

				getLogger().info(logPrefix + "Attempting startup load for Nicknames");

				File file = new File(configDir, nameDataFile);
				FileInputStream saveFile;
				try {
					saveFile = new FileInputStream(file);

					ObjectInputStream in = new ObjectInputStream(saveFile);

					@SuppressWarnings("unchecked")
					Map<UUID, String> mapUUIDNick = (Map<UUID, String>) in.readObject();
					@SuppressWarnings("unchecked")
					Map<UUID, String> mapUUIDName = (Map<UUID, String>) in.readObject();
					@SuppressWarnings("unchecked")
					Map<String, UUID> mapNickUUID = (Map<String, UUID>) in.readObject();
					@SuppressWarnings("unchecked")
					Map<String, UUID> mapNameUUID = (Map<String, UUID>) in.readObject();
					@SuppressWarnings("unchecked")
					Map<String, String> mapNickFormatted = (Map<String, String>) in.readObject();
					@SuppressWarnings("unchecked")
					Map<String, String> mapNameFormatted = (Map<String, String>) in.readObject();

					in.close();

					fileNameManager.setMapUUIDNick(mapUUIDNick);
					fileNameManager.setMapUUIDName(mapUUIDName);
					fileNameManager.setMapNickUUID(mapNickUUID);
					fileNameManager.setMapNameUUID(mapNameUUID);
					fileNameManager.setMapNickFormatted(mapNickFormatted);
					fileNameManager.setMapNameFormatted(mapNameFormatted);

				} catch (IOException | ClassNotFoundException e) {
					getLogger().info(logPrefix + "[ERROR] Could not load nickname data");
					e.printStackTrace();
				}

				//getLogger().info(logPrefix + "Load completed!");

			} else if (legacyNicknameFile.exists()) {

				// LEGACY NICKNAME FILE HANDLING --------------------------------------------


				HashMap<UUID, String> result = null;

				try {

					FileInputStream saveFile = new FileInputStream(legacyNicknameFile);
					ObjectInputStream in = new ObjectInputStream(saveFile);

					result = (HashMap<UUID, String>) in.readObject();
					in.close();
					getLogger().info(logPrefix + "Loaded a legacy (pre 1.6) nicknames file. Attempting conversion...");

					int counter = 0;

					if (result != null) {

						if (result.keySet() != null) {

							for (UUID u : result.keySet()) {

								counter++;
								nameManager.registerOfflinePlayerByUUID(u, "NotJoinedYet"+String.valueOf(counter));
								nameManager.setNickname(u, result.get(u));

							}

						}

					}

					File file = new File(configDir, nameDataFile);
					FileOutputStream saveFile2;
					try {
						saveFile2 = new FileOutputStream(file);

						ObjectOutputStream out = new ObjectOutputStream(saveFile2);

						out.writeObject(fileNameManager.getMapUUIDNick());
						out.writeObject(fileNameManager.getMapUUIDName());
						out.writeObject(fileNameManager.getMapNickUUID());
						out.writeObject(fileNameManager.getMapNameUUID());
						out.writeObject(fileNameManager.getMapNickFormatted());
						out.writeObject(fileNameManager.getMapNameFormatted());

						out.close();

					} catch (FileNotFoundException e) {
						getLogger().info(logPrefix + "[ERROR] Could not save nickname data");
						e.printStackTrace();
					}

				} catch (IOException|ClassNotFoundException e) {

					getLogger().info(logPrefix + "[ERROR] An error has occured reading the legacy nicknames file. Please delete it.");
					e.printStackTrace();

				}

			} else {

				getLogger().info(logPrefix + "Name data files do not exist to load. Must be first startup!");
				getLogger().info(logPrefix + "Enabling Nicknames! :D");
				getLogger().info(logPrefix + "Attempting to create hash files!");

				File file = new File(configDir, nameDataFile);
				FileOutputStream saveFile;
				try {
					saveFile = new FileOutputStream(file);

					ObjectOutputStream out = new ObjectOutputStream(saveFile);

					out.writeObject(fileNameManager.getMapUUIDNick());
					out.writeObject(fileNameManager.getMapUUIDName());
					out.writeObject(fileNameManager.getMapNickUUID());
					out.writeObject(fileNameManager.getMapNameUUID());
					out.writeObject(fileNameManager.getMapNickFormatted());
					out.writeObject(fileNameManager.getMapNameFormatted());

					out.close();

				} catch (FileNotFoundException e) {
					getLogger().info(logPrefix + "[ERROR] Could not save nickname data");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				getLogger().info(logPrefix + "The files were created!");

			}

			/* END LEGACY */

		}

		api.registerNameManager(nameManager);

		LocalProxyCommunicationManager proxyCommunicationManager = new SpigotBungeeCommunicationManager();
		api.registerProxyCommunicationManager(proxyCommunicationManager);

		LocalMetaManager metaManager = new LocalSpigotMetaManager();
		api.registerMetaManager(metaManager);

		// Register plugin communication channels
		registerCommunicationChannels();

		// Register Listeners

		// Register Commands

		// Manage dependencies...? Does anything need to be done here?

	}

	private void registerCommunicationChannels() {

		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:comm");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:chat");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:prefix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:suffix");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:dn");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:world");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:nick");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:pxe");
		getServer().getMessenger().registerOutgoingPluginChannel(this, "multichat:ppxe");

		// TODO NEED TO MAKE LOCAL VERSIONS OF THESE LISTENERS!!!
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:comm", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:chat", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:act", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:pact", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ch", MultiChatPluginMessageListener.getInstance());
		getServer().getMessenger().registerIncomingPluginChannel(this, "multichat:ignore", MultiChatPluginMessageListener.getInstance());

	}

	@Override
	public void onDisable() {

		// TODO make more generic!

		if (MultiChatLocal.getInstance().getNameManager().getMode() == LocalNameManagerMode.SQL) {

			try {
				DatabaseManager.getInstance().getDatabase("multichatspigot.db").get().disconnectFromDatabase();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} else {

			/* LEGACY! */

			LocalFileNameManager fileNameManager = (LocalFileNameManager) MultiChatLocal.getInstance().getNameManager();

			String nameDataFile = "namedata.dat";
			File file = new File(MultiChatLocal.getInstance().getConfigDirectory(), nameDataFile);
			FileOutputStream saveFile;
			try {
				saveFile = new FileOutputStream(file);

				ObjectOutputStream out = new ObjectOutputStream(saveFile);

				out.writeObject(fileNameManager.getMapUUIDNick());
				out.writeObject(fileNameManager.getMapUUIDName());
				out.writeObject(fileNameManager.getMapNickUUID());
				out.writeObject(fileNameManager.getMapNameUUID());
				out.writeObject(fileNameManager.getMapNickFormatted());
				out.writeObject(fileNameManager.getMapNameFormatted());

				out.close();


			} catch (IOException e) {
				getLogger().info("[ERROR] Could not save nickname data");
				e.printStackTrace();
			}

		}

	}

}
