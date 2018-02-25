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
        
        String sql = "SELECT * FROM Ingredient WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }

        Ingredient ingredient = ingredientFromResult(rs);

        rs.close();
        stmt.close();
        connection.close();

        return ingredient;
    }

    @Override
    public List<Ingredient> findAll() throws SQLException {
        Connection connection = database.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        
        String sql = "SELECT * FROM Ingredient;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Ingredient ingredient = ingredientFromResult(rs);
            ingredients.add(ingredient);
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return ingredients;
    }

    public Integer howMany() throws SQLException {
        return null;
    }

//    @Override
//    public Ingredient saveOrUpdate(Ingredient ingredient) throws SQLException {
//        //Tän pitäis toimia, jos on sopiva koodi Mainissa eli kun halutaan tallentaa,
//        //niin olis tyyliin ingredientdao.saveOrUpdate(new Ingredient(null, "name"));
//        //Mutta tää on siis vaan ehdotus yhden vanhan tehtävän pohjalta eli ei oo välttämättä hyvä.
//        //Toisin sanoen saa kaikin mokomin muuttaa paremmaksi :D
//          
//        
//        if (ingredient.getId() == null) {   
//            return save(ingredient);
//        } else {
//            return update(ingredient);
//        }
//    }
    
     public Ingredient save(Ingredient ingredient) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "INSERT INTO Ingredient (name) VALUES (?);";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, ingredient.getName());
                
        stmt.executeUpdate();

        stmt.close();
        connection.close();

        return ingredient;
    }

    private Ingredient update(Ingredient ingredient) throws SQLException {
        Connection connection = database.getConnection();
        Integer id = ingredient.getId();
        
        String sql = "UPDATE Ingredient SET id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        
        stmt.executeUpdate();

        stmt.close();
        connection.close();

        return findOne(id);
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

        stmt.close();
        connection.close();
    }
    
    private Ingredient ingredientFromResult(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        
        return new Ingredient(id, name);
    }
    
}
