package tikape.sql5.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.sql5.domain.Ingredient;

public class IngredientDao implements Dao<Ingredient, Integer> {
    
    private Database database;

    public IngredientDao(Database database) {
        this.database = database;
    }

    @Override
    public Ingredient findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "SELECT * FROM Ingredient WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }

        Ingredient ingredient = ingredientFromResult(rs);
        closeAll(rs, stmt, connection);
        return ingredient;
    }

    @Override
    public List<Ingredient> findAll() throws SQLException {
        Connection connection = database.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        
        String sql = "SELECT * FROM Ingredient ORDER BY name ASC;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Ingredient ingredient = ingredientFromResult(rs);
            ingredients.add(ingredient);
        }
        
        closeAll(rs, stmt, connection);
        return ingredients;
    }
    
     public Ingredient save(Ingredient ingredient) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "INSERT INTO Ingredient (name) VALUES (?);";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, ingredient.getName());
                
        stmt.executeUpdate();

        closeAll(stmt, connection);
        return ingredient;
    }

    @Override
    public Ingredient saveOrUpdate(Ingredient object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "DELETE FROM Ingredient WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, key);
        
        stmt.executeUpdate();
        closeAll(stmt, connection);
    }
    
    private Ingredient ingredientFromResult(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        
        return new Ingredient(id, name);
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
