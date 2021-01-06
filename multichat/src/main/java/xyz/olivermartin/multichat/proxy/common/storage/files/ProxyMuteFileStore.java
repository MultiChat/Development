package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyMuteFileStore extends ProxyGenericFileStore {

	public ProxyMuteFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		Set<UUID> result = null;

		try {

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (Set<UUID>)in.readObject();
			in.close();
			ChatControl.setMutedPlayers(result);
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
			out.writeObject(ChatControl.getMutedPlayers());
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
