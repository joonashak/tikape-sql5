package tikape.sql5.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tikape.sql5.domain.Ingredient;
import tikape.sql5.domain.Smoothie;

public class SmoothieDao implements Dao<Smoothie, Integer> {

    private Database database;

    public SmoothieDao (Database database) {
        this.database = database;
    }
    
    /**
     * Find one Smoothie by key.
     * @param key Database id
     * @return Smoothie or null if none found
     * @throws SQLException 
     */
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

    /**
     * Get all Smoothies.
     * @return List of Smoothies
     * @throws SQLException
     */
    @Override
    public List<Smoothie> findAll() throws SQLException {
        Connection connection = database.getConnection();
        List<Smoothie> smoothies = new ArrayList<>();
        
        String sql = "SELECT * FROM Smoothie ORDER BY name ASC;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Smoothie smoothie = smoothieFromResults(rs);
            smoothies.add(smoothie);
        }
        
        closeAll(rs, stmt, connection);
        return smoothies;
    }
    
    /**
     * Find one Smoothie by name.
     * @param name String to search with
     * @return Smoothie or null if none found
     * @throws SQLException 
     */
    public Smoothie findByName(String name) throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = "SELECT * FROM Smoothie WHERE name = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, name);
        
        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return null;
        }
        
        Smoothie smoothie = smoothieFromResults(rs);
        closeAll(rs, stmt, connection);
        return smoothie;
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
    
    // TODO: Move to new StatisticsDao (or at least IngredientDao)?
    public List<HashMap> howmany() throws SQLException {
        Connection connection = database.getConnection();
        List<HashMap> ingredientCounts = new ArrayList<>();
                
        String sql = 
                  "SELECT Ingredient.name, COUNT(SmoothieIngredient.ingredient_id) AS count "
                + "FROM Ingredient "
                + "LEFT JOIN SmoothieIngredient ON Ingredient.id = SmoothieIngredient.ingredient_id "
                + "GROUP BY Ingredient.name "
                + "ORDER BY Ingredient.name ASC;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()){
            HashMap ingredient = new HashMap<>();
            ingredient.put("name", rs.getString("name"));
            ingredient.put("count", rs.getInt("count"));
            
            ingredientCounts.add(ingredient);
        }
        
        closeAll(rs, stmt, connection);
        
        return ingredientCounts;
    }
    
    /**
     * Inserts a row with the given Smoothie.getName() if the name doesn't exist
     * yet. 
     * @param smoothie Smoothie to insert into database
     * @return Inserted or existing Smoothie
     * @throws SQLException 
     */
    public Smoothie save(Smoothie smoothie) throws SQLException {
        String name = smoothie.getName().trim();
        Smoothie foundSmoothie = findByName(name);
        
        if (foundSmoothie != null) {
            return foundSmoothie;
        }
        
        Connection connection = database.getConnection();
        
        String sql = "INSERT INTO Smoothie (name) VALUES (?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, name);
                
        stmt.executeUpdate();
        closeAll(stmt, connection);
        return findByName(name);
    }
    
    public void addIngredient(
            Integer smoothieId, 
            Ingredient ingredient, 
            Integer order
    ) throws SQLException {
        
        Integer nextRow = nextRecipeRow(smoothieId);
        Connection connection = database.getConnection();
        
        // Figure out the correct row ordering and shift existing rows,
        // if necessary
        if (order < nextRow && nextRow > 1 && order > 0) {
            incrementRowOrder(smoothieId, order);
        } else {
            order = nextRow;
        }
        
        String sql = 
                  "INSERT INTO SmoothieIngredient "
                + "  (smoothie_id,ingredient_id,ordering,amount,info) "
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
    
    private void incrementRowOrder(Integer smoothieId, Integer order) 
            throws SQLException {
        Connection connection = database.getConnection();
        
        String sql = 
                  "UPDATE SmoothieIngredient "
                + "SET ordering = ordering + 1 "
                + "WHERE smoothie_id = ? AND ordering >= ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, smoothieId);
        stmt.setInt(2, order);

        stmt.execute();
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
    
    private Integer nextRecipeRow(int smoothieId) throws SQLException {
        Connection connection = this.database.getConnection();
        Integer nextRow;
        
        String sql = 
                  "SELECT ordering AS last_row "
                + "FROM SmoothieIngredient "
                + "WHERE smoothie_id = ? "
                + "ORDER BY ordering DESC "
                + "LIMIT 1;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, smoothieId);
        
        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next()) {
            return 1;
        }
        
        nextRow = rs.getInt("last_row") + 1;
        closeAll(rs, stmt, connection);
        return nextRow;
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

    @Override
    public Smoothie saveOrUpdate(Smoothie object) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
