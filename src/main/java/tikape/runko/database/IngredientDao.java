package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Ingredient;

public class IngredientDao implements Dao<Ingredient, Integer> {
    
    private Database database;

    public IngredientDao(Database database) {
        this.database = database;
    }

    @Override
    public Ingredient findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Ingredient WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }

        Ingredient ingredient = new Ingredient(rs.getInt("id"), rs.getString("name"));

        rs.close();
        stmt.close();
        connection.close();

        return ingredient;
    }

    @Override
    public List<Ingredient> findAll() throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Ingredient");
        
        ResultSet rs = stmt.executeQuery();
        
        
        List<Ingredient> ingredients = new ArrayList<>();
        
        while (rs.next()){
                       
            Ingredient ingr = new Ingredient(rs.getInt("id"), rs.getString("name"));
            
            ingredients.add(ingr);
        }
        
        if (ingredients.isEmpty()){
            return null;
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        return ingredients;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Ingredient WHERE id = ?");
        stmt.setInt(1, key);
        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    @Override
    public Ingredient saveOrUpdate(Ingredient ingredient) throws SQLException {
        //Tän pitäis toimia, jos on sopiva koodi Mainissa eli kun halutaan tallentaa,
        //niin olis tyyliin ingredientdao.saveOrUpdate(new Ingredient(null, "name"));
        //Mutta tää on siis vaan ehdotus yhden vanhan tehtävän pohjalta eli ei oo välttämättä hyvä.
        //Toisin sanoen saa kaikin mokomin muuttaa paremmaksi :D
          
        
        if (ingredient.getId() == null) {   
            return save(ingredient);
        } else {
            return update(ingredient);
        }
    }
    
     private Ingredient save(Ingredient ingredient) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Ingredient"
                + " (name)"
                + " VALUES (?)");
        stmt.setString(1, ingredient.getName());
                
        stmt.executeUpdate();
        stmt.close();

        stmt = conn.prepareStatement("SELECT * FROM Ingredient"
                + " WHERE name = ?");
        stmt.setString(1, ingredient.getName());
        
        ResultSet rs = stmt.executeQuery();
        rs.next(); 

        Ingredient i = new Ingredient(rs.getInt("id"), rs.getString("name"));

        stmt.close();
        rs.close();

        conn.close();

        return i;
    }

    private Ingredient update(Ingredient ingredient) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE Ingredient SET"
                + " id = ?");
        stmt.setInt(1, ingredient.getId());
        
        
        stmt.executeUpdate();

        stmt.close();
        conn.close();

        return ingredient;
    }
    
}
