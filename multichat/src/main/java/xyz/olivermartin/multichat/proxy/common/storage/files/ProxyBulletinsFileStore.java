package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import xyz.olivermartin.multichat.bungee.Bulletins;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyBulletinsFileStore extends ProxyGenericFileStore {

	public ProxyBulletinsFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		ArrayList<String> result = null;
		boolean enabled = false;
		int timeBetween = 0;

		try {

			FileInputStream stream = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(stream);
			enabled = in.readBoolean();
			timeBetween = in.readInt();
			result = (ArrayList<String>)in.readObject();
			in.close();
			Bulletins.setArrayList(result);
			if (enabled) Bulletins.startBulletins(timeBetween);
			return true;

		} catch (IOException|ClassNotFoundException e) {

			e.printStackTrace();
			return false;

		}

	}

	@Override
	protected boolean saveFile(File file) {

		try	{

			FileOutputStream stream = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeBoolean(Bulletins.isEnabled());
			out.writeInt(Bulletins.getTimeBetween());
			out.writeObject(Bulletins.getArrayList());
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
