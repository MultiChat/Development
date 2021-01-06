package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.UUID;

import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyGlobalChatFileStore extends ProxyGenericFileStore {

	public ProxyGlobalChatFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		Map<UUID, Boolean> result = null;

		try {

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (Map<UUID, Boolean>)in.readObject();
			in.close();
			ChatModeManager.getInstance().loadData(result);
			return true;

		} catch (IOException|ClassNotFoundException e) {

			e.printStackTrace();
			return false;

		}

	}

	@Override
	protected boolean saveFile(File file) {

		try {

			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(ChatModeManager.getInstance().getData());
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
