package xyz.olivermartin.multichat.proxy.bungee.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.store.ProxyNameFileDataStore;

public class ProxyBungeeNameFileDataStore extends ProxyNameFileDataStore {

	public ProxyBungeeNameFileDataStore(File path, String filename) {
		super(MultiChatProxyPlatform.BUNGEE, path, filename);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		FileInputStream fileInputStream;

		try {

			fileInputStream = new FileInputStream(file);

			ObjectInputStream in = new ObjectInputStream(fileInputStream);

			Map<UUID, String> lastNames = (Map<UUID, String>) in.readObject();

			in.close();

			setLastNames(lastNames);

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

			out.writeObject(getLastNames());

			out.close();

			return true;

		} catch (IOException e) {

			return false;

		}

	}

}
