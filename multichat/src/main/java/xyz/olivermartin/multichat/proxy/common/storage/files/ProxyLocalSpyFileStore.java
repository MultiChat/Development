package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyLocalSpyFileStore extends ProxyGenericFileStore {

	public ProxyLocalSpyFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		List<UUID> result = null;

		try {

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (List<UUID>)in.readObject();
			in.close();
			ds.setLocalSpy(result);
			return true;

		} catch (IOException|ClassNotFoundException e) {

			e.printStackTrace();
			return false;

		}

	}

	@Override
	protected boolean saveFile(File file) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		try {

			FileOutputStream saveFile = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(saveFile);
			out.writeObject(ds.getLocalSpy());
			out.close();
			return true;

		} catch (IOException e)	{

			e.printStackTrace();
			return false;

		}

	}

}
