package xyz.olivermartin.multichat.local.platform.spigot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;

import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.storage.LocalFileNameManager;
import xyz.olivermartin.multichat.local.storage.LocalNicknameFile;

public class LocalSpigotNicknameFile extends LocalNicknameFile {

	public LocalSpigotNicknameFile(File configPath, String fileName, LocalFileNameManager lfnm) {
		super(configPath, fileName, lfnm, MultiChatLocalPlatform.SPIGOT);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		FileInputStream fileInputStream;

		try {

			fileInputStream = new FileInputStream(file);

			ObjectInputStream in = new ObjectInputStream(fileInputStream);

			Map<UUID, String> mapUUIDNick = (Map<UUID, String>) in.readObject();
			Map<UUID, String> mapUUIDName = (Map<UUID, String>) in.readObject();
			Map<String, UUID> mapNickUUID = (Map<String, UUID>) in.readObject();
			Map<String, UUID> mapNameUUID = (Map<String, UUID>) in.readObject();
			Map<String, String> mapNickFormatted = (Map<String, String>) in.readObject();
			Map<String, String> mapNameFormatted = (Map<String, String>) in.readObject();

			in.close();

			lfnm.setMapUUIDNick(mapUUIDNick);
			lfnm.setMapUUIDName(mapUUIDName);
			lfnm.setMapNickUUID(mapNickUUID);
			lfnm.setMapNameUUID(mapNameUUID);
			lfnm.setMapNickFormatted(mapNickFormatted);
			lfnm.setMapNameFormatted(mapNameFormatted);

			fileInputStream.close();

			return true;

		} catch (IOException | ClassNotFoundException e) {

			return false;

		}
	}

	@Override
	protected boolean saveFile(File file) {

		FileOutputStream fileOutputStream;

		try {

			fileOutputStream = new FileOutputStream(file);

			ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);

			out.writeObject(lfnm.getMapUUIDNick());
			out.writeObject(lfnm.getMapUUIDName());
			out.writeObject(lfnm.getMapNickUUID());
			out.writeObject(lfnm.getMapNameUUID());
			out.writeObject(lfnm.getMapNickFormatted());
			out.writeObject(lfnm.getMapNameFormatted());

			out.close();

			return true;

		} catch (IOException e) {

			return false;

		}

	}

}
