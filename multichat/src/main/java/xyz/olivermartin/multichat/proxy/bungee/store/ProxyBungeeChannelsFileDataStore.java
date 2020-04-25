package xyz.olivermartin.multichat.proxy.bungee.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.store.ProxyChannelsFileDataStore;

public class ProxyBungeeChannelsFileDataStore extends ProxyChannelsFileDataStore{

	public ProxyBungeeChannelsFileDataStore(File path, String filename) {
		super(MultiChatProxyPlatform.BUNGEE, path, filename);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		FileInputStream fileInputStream;

		try {

			fileInputStream = new FileInputStream(file);

			ObjectInputStream in = new ObjectInputStream(fileInputStream);

			Map<UUID, String> currentChannels = (Map<UUID, String>) in.readObject();
			Map<String, Set<UUID>> hiddenChannels = (Map<String, Set<UUID>>) in.readObject();

			in.close();

			setCurrentChannels(currentChannels);
			setHiddenChannels(hiddenChannels);

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

			out.writeObject(getCurrentChannels());
			out.writeObject(getHiddenChannels());

			out.close();

			return true;

		} catch (IOException e) {

			return false;

		}

	}

}
