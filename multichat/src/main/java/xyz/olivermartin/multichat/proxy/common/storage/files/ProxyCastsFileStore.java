package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import xyz.olivermartin.multichat.bungee.CastControl;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyCastsFileStore extends ProxyGenericFileStore {

	public ProxyCastsFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		HashMap<String, String> result = null;

		try	{

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<String, String>)in.readObject();
			in.close();
			CastControl.castList = result;
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
			out.writeObject(CastControl.castList);
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
