package xyz.olivermartin.multichat.local.platform.sponge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.storage.LocalFileNameManager;
import xyz.olivermartin.multichat.local.storage.LocalNicknameFile;

public class LocalSpongeNicknameFile extends LocalNicknameFile {

	public LocalSpongeNicknameFile(File configPath, String fileName, LocalFileNameManager lfnm) {
		super(configPath, fileName, lfnm, MultiChatLocalPlatform.SPONGE);
	}

	@SuppressWarnings("serial")
	@Override
	protected boolean loadFile(File file) {

		HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode rootNode;

		Map<UUID, String> mapUUIDNick;
		Map<String, UUID> mapNickUUID;
		Map<String, String> mapNickFormatted;

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

			lfnm.setMapUUIDNick(mapUUIDNick);
			lfnm.setMapNickUUID(mapNickUUID);
			lfnm.setMapNickFormatted(mapNickFormatted);

			configLoader.save(rootNode);

			return true;

		} catch (ObjectMappingException | IOException e) {

			return false;

		}

	}

	@SuppressWarnings("serial")
	@Override
	protected boolean saveFile(File file) {

		HoconConfigurationLoader configLoader = HoconConfigurationLoader.builder().setFile(file).build();
		ConfigurationNode rootNode;

		try {

			rootNode = configLoader.createEmptyNode();

			rootNode.getNode("mapUUIDNick").setValue(new TypeToken<Map<UUID,String>>() {}, 
					(lfnm.getMapUUIDNick()));
			rootNode.getNode("mapNickUUID").setValue(new TypeToken<Map<String,UUID>>() {}, 
					(lfnm.getMapNickUUID()));
			rootNode.getNode("mapNickFormatted").setValue(new TypeToken<Map<String,String>>() {}, 
					(lfnm.getMapNickFormatted()));

			configLoader.save(rootNode);

			return true;

		} catch (ObjectMappingException | IOException e) {

			return false;

		}

	}

}
