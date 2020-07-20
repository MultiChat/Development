package xyz.olivermartin.multichat.proxy.common.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import xyz.olivermartin.multichat.bungee.Announcements;

public class ProxyAnnouncementsFileStore extends ProxyGenericFileStore {

	public ProxyAnnouncementsFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		HashMap<String, String> result = null;

		try {

			FileInputStream stream = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(stream);
			result = (HashMap<String, String>)in.readObject();
			in.close();
			Announcements.loadAnnouncementList(result);
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
			out.writeObject(Announcements.getAnnouncementList());
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
