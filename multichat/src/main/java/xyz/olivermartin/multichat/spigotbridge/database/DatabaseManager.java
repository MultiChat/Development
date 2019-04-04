package xyz.olivermartin.multichat.spigotbridge.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

	private static DatabaseManager instance;
	
	public static DatabaseManager getInstance() {
		return instance;
	}
	
	static {
		instance = new DatabaseManager();
	}
	
	/* END STATIC */
	
	private DatabaseManager() {
		/* EMPTY */
	}
	
	public static void main(String args[]) {
		
		DatabaseManager.getInstance().createDatabase("testing.db");
		
	}
	
	/**
	 * Generic class to create a sqlite database
	 */
    public void createDatabase(String fileName) {
 
        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;
 
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
}
