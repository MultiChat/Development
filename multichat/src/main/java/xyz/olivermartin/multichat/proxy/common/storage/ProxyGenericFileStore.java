package xyz.olivermartin.multichat.proxy.common.storage;

import java.io.File;

import xyz.olivermartin.multichat.bungee.DebugManager;

public abstract class ProxyGenericFileStore implements ProxyFileStore {

	private String fileName;
	private File fileDirectory;

	public ProxyGenericFileStore(String fileName, File fileDirectory) {
		this.fileName = fileName;
		this.fileDirectory = fileDirectory;
		startupFile();
	}

	public String getFileName() {
		return fileName;
	}

	public File getFileDirectory() {
		return this.fileDirectory;
	}

	public File getFile() {
		return new File(fileDirectory, fileName);
	}

	protected boolean startupFile() {

		File file = getFile();

		boolean status;

		if (!file.exists()) {
			status = saveFile(file);
			DebugManager.log("[ProxyFileStore] [" + getFileName() + "] Created new file (was successful = " + status + ")");
		}

		status = loadFile(file);
		DebugManager.log("[ProxyFileStore] [" + getFileName() + "] Loaded file (was successful = " + status + ")");

		return status;

	}

	/**
	 * Save the data to file
	 * @return true if successful
	 */
	public boolean save() {
		return saveFile(getFile());
	}

	/**
	 * Load the data from the file into the system (overwrites anything currently loaded)
	 * @return true if successful
	 */
	public boolean reload() {
		return startupFile();
	}

	/**
	 * Load the file contents into the correct place
	 * @param file
	 * @return true if successful
	 */
	protected abstract boolean loadFile(File file);

	/**
	 * Save the file contents from the correct place
	 * @param file
	 * @return true if successful
	 */
	protected abstract boolean saveFile(File file);

}
