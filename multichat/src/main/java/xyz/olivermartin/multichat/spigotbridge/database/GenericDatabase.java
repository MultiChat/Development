package xyz.olivermartin.multichat.spigotbridge.database;

public abstract class GenericDatabase {

	protected String url;
	private boolean ready;
	
	public GenericDatabase(String url) {
		this.url = url;
		ready = setupDatabase(url);
	}
	
	public String getURL() {
		return this.url;
	}
	
	public boolean isReady() {
		return this.ready;
	}
	
	protected abstract boolean setupDatabase(String url);
	
	protected abstract void disconnect();
	
	protected abstract boolean connect();
	
	public void connectToDatabase() {
		this.ready = connect();
	}
	
	public void disconnectFromDatabase() {
		disconnect();
		this.ready = false;
	}
	
}
