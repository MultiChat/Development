package xyz.olivermartin.multichat.proxy.common.store;

import java.io.File;

import xyz.olivermartin.multichat.common.DataStoreMode;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.ProxyConsoleLogger;

public abstract class ProxyAbstractFileDataStore extends ProxyAbstractDataStore {

	private File path;
	private String filename;
	private MultiChatProxyPlatform platform;

	public ProxyAbstractFileDataStore(MultiChatProxyPlatform platform, File path, String filename) {
		super(DataStoreMode.FILE);
		this.platform = platform;
		this.path = path;
		this.filename = filename;
	}

	public MultiChatProxyPlatform getPlatform() {
		return this.platform;
	}

	protected File getFile() {
		return new File(path, filename);
	}

	protected abstract boolean loadFile(File file);

	protected abstract boolean saveFile(File file);

	@Override
	protected boolean init() {

		ProxyConsoleLogger consoleLogger = MultiChatProxy.getInstance().getConsoleLogger();

		consoleLogger.debug("[FileDataStore] Init for file: " + filename);

		File file = getFile();

		boolean status;

		if (!file.exists()) {
			consoleLogger.debug("[FileDataStore] File: " + filename + ", does not exist... Creating...");
			status = saveFile(file);
		}

		status = loadFile(file);

		consoleLogger.debug("[FileDataStore] Did the file load correctly?: " + status);

		return status;

	}

	@Override
	public boolean reload() {
		return init();
	}

	@Override
	public boolean save() {
		return saveFile(getFile());
	}

}
