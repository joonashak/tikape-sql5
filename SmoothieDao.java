package tikape.sql5.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.sql5.domain.Ingredient;
import tikape.sql5.domain.Smoothie;

public class SmoothieDao implements Dao<Smoothie, Integer> {

    private Database database;

    public SmoothieDao (Database database) {
        this.database = database;
    }
    
    @Override
    public Smoothie findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "SELECT * FROM Smoothie WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }

        Smoothie smoothie = smoothieFromResults(rs);
        closeAll(rs, stmt, connection);
        return smoothie;
    }

    @Override
    public List<Smoothie> findAll() throws SQLException {
        Connection connection = database.getConnection();
        List<Smoothie> smoothies = new ArrayList<>();
        
        String sql = "SELECT * FROM Smoothie;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Smoothie smoothie = smoothieFromResults(rs);
            smoothies.add(smoothie);
        }
        
        closeAll(rs, stmt, connection);
        return smoothies;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "DELETE FROM Smoothie WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, key);
        
        stmt.executeUpdate();

        closeAll(stmt, connection);
    }
    
    public Integer howmany(Integer id) throws SQLException {
        Connection connection = database.getConnection();
        
        String n = "SELECT Ingredient.name, COUNT() FROM SmoothieINgredient INNER JOIN INgredient ON Ingredient.id = SmoothieIngredient.ingredient_id GROUP BY SmoothieIngredient.ingredient_id;";
        PreparedStatement stmt = connection.prepareStatement(n);
        stmt.setInt(1, id);
        
        stmt.executeQuery();

        closeAll(stmt, connection);
        
        return Integer.parseInt(n);
    }

//    @Override
//    public Smoothie saveOrUpdate(Smoothie smoothie) throws SQLException {
//        //Kts. IngredientDaon SaveOrUpdate-metodin kommentit       
//         
//        
//        if (smoothie.getId() == null) {   
//            return save(smoothie);
//        } else {
//            return update(smoothie);
//        }
//    }
    
    public Smoothie save(Smoothie smoothie) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "INSERT INTO Smoothie (name) VALUES (?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, smoothie.getName());
                
        stmt.executeUpdate();
        closeAll(stmt, connection);
        return smoothie;
    }

    private Smoothie update(Smoothie smoothie) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "UPDATE Smoothie SET id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, smoothie.getId());
        
        stmt.executeUpdate();
        closeAll(stmt, connection);
        return smoothie;
    }

    @Override
    public Smoothie saveOrUpdate(Smoothie object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void addIngredient(
            Integer smoothieId, 
            Ingredient ingredient, 
            Integer order
    ) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "INSERT INTO SmoothieIngredient "
                + "(smoothie_id,ingredient_id,ordering,amount,info) "
                + "VALUES (?,?,?,?,?);";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, smoothieId);
        stmt.setInt(2, ingredient.getId());
        stmt.setInt(3, order);
        stmt.setString(4, ingredient.getAmount());
        stmt.setString(5, ingredient.getInfo());
        
        stmt.executeUpdate();
        closeAll(stmt, connection);
    }
    
    private List<Ingredient> getIngredients(Smoothie smoothie) 
            throws SQLException {
        
        Connection connection = database.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        Integer key = smoothie.getId();
        
        String sql = 
                  "SELECT * "
                + "FROM Ingredient "
                + "INNER JOIN SmoothieIngredient "
                + "ON Ingredient.id = SmoothieIngredient.ingredient_id "
                + "WHERE SmoothieIngredient.smoothie_id = ? "
                + "ORDER BY ordering;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            ingredients.add(
                    new Ingredient(
                            rs.getInt("id"), 
                            rs.getString("name"), 
                            rs.getString("amount"),
                            rs.getString("info")
                    )
            );
        }

        rs.close();
        stmt.close();
        connection.close();

        return ingredients;
    }
    
    private Smoothie smoothieFromResults(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        Smoothie smoothie = new Smoothie(id, name);
        
        List<Ingredient> ingredients = getIngredients(smoothie);
        smoothie.setIngredients(ingredients);
        
        return smoothie;
    }
    
    private void closeAll(PreparedStatement stmt, Connection connection) 
            throws SQLException {
        stmt.close();
        connection.close();
    }
    
    private void closeAll(
            ResultSet rs, 
            PreparedStatement stmt, 
            Connection connection
    ) throws SQLException {
        rs.close();
        closeAll(stmt, connection);
    }
}
