package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import xyz.olivermartin.multichat.bungee.UUIDNameManager;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyUUIDNameFileStore extends ProxyGenericFileStore {

	public ProxyUUIDNameFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		HashMap<UUID, String> result = null;

		try {

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<UUID,String>)in.readObject();
			in.close();
			UUIDNameManager.uuidname.putAll(result);
			return true;

		} catch (IOException|ClassNotFoundException e) {

			e.printStackTrace();
			return false;

		}

	}

	@Override
	protected boolean saveFile(File file) {

		try {

			FileOutputStream stream = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(UUIDNameManager.uuidname);
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
