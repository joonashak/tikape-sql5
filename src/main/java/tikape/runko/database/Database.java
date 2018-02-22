package tikape.runko.database;

import java.sql.*;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }
    
    public Database() throws ClassNotFoundException{
        this.databaseAddress = "jdbc:sqlite:devDatabase.db";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
    }
}
