package tikape.sql5.database;

import java.sql.*;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }
    
    public Database() {
        
        if (System.getenv("DATABASE_URL") == null) {
            // Use test db file
            this.databaseAddress = "jdbc:sqlite:devDatabase.db";
            return;
        }
        
        this.databaseAddress = System.getenv("JDBC_DATABASE_URL");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }
}
