package xyz.olivermartin.multichat.local.sponge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import xyz.olivermartin.multichat.local.LocalFileNameManager;
import xyz.olivermartin.multichat.local.LocalNameManager;
import xyz.olivermartin.multichat.local.LocalNameManagerMode;
import xyz.olivermartin.multichat.local.LocalNicknameFile;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

public class LocalSpongeNicknameFile extends LocalNicknameFile {

	public LocalSpongeNicknameFile(File configPath, String fileName) {
		super(configPath, fileName, MultiChatLocalPlatform.SPONGE);
	}

	@SuppressWarnings("serial")
	@Override
	protected boolean loadFile(File file) {

		HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode rootNode;

		Map<UUID, String> mapUUIDNick;
		Map<String, UUID> mapNickUUID;
		Map<String, String> mapNickFormatted;

		LocalNameManager nameManager = MultiChatLocal.getInstance().getNameManager();

		if (nameManager.getMode() == LocalNameManagerMode.FILE) {

			LocalFileNameManager fileNameManager = (LocalFileNameManager) nameManager;

			try {

				rootNode = configLoader.load();

				mapUUIDNick = (Map<UUID, String>) rootNode.getNode("mapUUIDNick").getValue(new TypeToken<Map<UUID,String>>() { /* EMPTY */ });
				mapNickUUID = (Map<String, UUID>) rootNode.getNode("mapNickUUID").getValue(new TypeToken<Map<String, UUID>>() { /* EMPTY */ });
				mapNickFormatted = (Map<String, String>) rootNode.getNode("mapNickFormatted").getValue(new TypeToken<Map<String,String>>() { /* EMPTY */ });

				if (mapUUIDNick == null) {
					mapUUIDNick = new HashMap<UUID,String>();
					mapNickUUID = new HashMap<String, UUID>();
					mapNickFormatted = new HashMap<String,String>();
				}

				fileNameManager.setMapUUIDNick(mapUUIDNick);
				fileNameManager.setMapNickUUID(mapNickUUID);
				fileNameManager.setMapNickFormatted(mapNickFormatted);

				configLoader.save(rootNode);

				return true;

			} catch (ObjectMappingException | IOException e) {

				return false;

			}

		} else {

			return false;

		}

	}

	@SuppressWarnings("serial")
	@Override
	protected boolean saveFile(File file) {

		HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode rootNode;

		LocalNameManager nameManager = MultiChatLocal.getInstance().getNameManager();

		if (nameManager.getMode() == LocalNameManagerMode.FILE) {

			LocalFileNameManager fileNameManager = (LocalFileNameManager) nameManager;

			try {

				rootNode = configLoader.createEmptyNode();

				rootNode.getNode("mapUUIDNick").setValue(new TypeToken<Map<UUID,String>>() {}, 
						(fileNameManager.getMapUUIDNick()));
				rootNode.getNode("mapNickUUID").setValue(new TypeToken<Map<String,UUID>>() {}, 
						(fileNameManager.getMapNickUUID()));
				rootNode.getNode("mapNickFormatted").setValue(new TypeToken<Map<String,String>>() {}, 
						(fileNameManager.getMapNickFormatted()));

				configLoader.save(rootNode);

				return true;

			} catch (ObjectMappingException | IOException e) {

				return false;

			}

		} else {

			return false;

		}

	}

}
