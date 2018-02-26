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
        
        String addr = System.getenv("DATABASE_URL");
        
        // Heroku supplies DATABASE_URL with postgres:// prefix, which is not
        // supported by JDBC.
        addr = addr.replaceFirst("postgres://", "postgresql://");
        
        this.databaseAddress = "jdbc:" + addr;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }
}
