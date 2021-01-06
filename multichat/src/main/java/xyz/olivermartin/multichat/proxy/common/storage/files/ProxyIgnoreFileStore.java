package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyIgnoreFileStore extends ProxyGenericFileStore {

	public ProxyIgnoreFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {
		if (ProxyConfigs.CHAT_CONTROL.isSessionIgnore()) {
			ChatControl.setIgnoreMap(new HashMap<>());
			return true;
		}

		Map<UUID, Set<UUID>> result = null;

		try {

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (Map<UUID, Set<UUID>>)in.readObject();
			in.close();
			ChatControl.setIgnoreMap(result);
			return true;

		} catch (IOException|ClassNotFoundException e) {

			e.printStackTrace();
			return false;

		}

	}

	@Override
	protected boolean saveFile(File file) {
		if (ProxyConfigs.CHAT_CONTROL.isSessionIgnore()) return true;

		try {

			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(ChatControl.getIgnoreMap());
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
