package xyz.olivermartin.multichat.proxy.common.storage.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import com.olivermartin410.plugins.TChatInfo;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyGenericFileStore;

public class ProxyAdminChatFileStore extends ProxyGenericFileStore {

	public ProxyAdminChatFileStore(String fileName, File fileDirectory) {
		super(fileName, fileDirectory);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean loadFile(File file) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		HashMap<UUID, TChatInfo> result = null;

		try {

			FileInputStream saveFile = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(saveFile);
			result = (HashMap<UUID, TChatInfo>)in.readObject();
			in.close();
			ds.setAdminChatPreferences(result);
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

			FileOutputStream stream = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(stream);
			out.writeObject(ds.getAdminChatPreferences());
			out.close();
			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		}

	}

}
