package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Ingredient;
import tikape.runko.domain.Smoothie;

public class SmoothieDao implements Dao<Smoothie, Integer> {

    private Database database;

    public SmoothieDao (Database database) {
        this.database = database;
    }
    
    @Override
    public Smoothie findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Smoothie WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }

        Smoothie smoothie = new Smoothie(rs.getInt("id"), rs.getString("name"));

        rs.close();
        stmt.close();
        connection.close();

        return smoothie;
    }

    @Override
    public List<Smoothie> findAll() throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Smoothie");
        
        ResultSet rs = stmt.executeQuery();
        
        
        List<Smoothie> smoothies = new ArrayList<>();
        
        while (rs.next()){
                       
            Smoothie smoo = new Smoothie(rs.getInt("id"), rs.getString("name"));
            
            smoothies.add(smoo);
        }
        
        if (smoothies.isEmpty()){
            return null;
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return smoothies;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Smoothie WHERE id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    @Override
    public Smoothie saveOrUpdate(Smoothie smoothie) throws SQLException {
        //Kts. IngredientDaon SaveOrUpdate-metodin kommentit       
         
        
        if (smoothie.getId() == null) {   
            return save(smoothie);
        } else {
            return update(smoothie);
        }
    }
    
    private Smoothie save(Smoothie smoothie) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Smoothie"
                + " (name)"
                + " VALUES (?)");
        stmt.setString(1, smoothie.getName());
                
        stmt.executeUpdate();
        stmt.close();

        stmt = conn.prepareStatement("SELECT * FROM Smoothie"
                + " WHERE name = ?");
        stmt.setString(1, smoothie.getName());
        
        ResultSet rs = stmt.executeQuery();
        rs.next(); 

        Smoothie s = new Smoothie(rs.getInt("id"), rs.getString("name"));

        stmt.close();
        rs.close();

        conn.close();

        return s;
    }

    private Smoothie update(Smoothie smoothie) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Smoothie SET"
                + " id = ?");
        stmt.setInt(1, smoothie.getId());
        
        
        stmt.executeUpdate();

        stmt.close();
        conn.close();

        return smoothie;
    
    }
    
}
