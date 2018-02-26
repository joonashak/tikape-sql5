package tikape.sql5;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import tikape.sql5.database.Database;
import tikape.sql5.database.IngredientDao;
import tikape.sql5.database.SmoothieDao;
import tikape.sql5.domain.Ingredient;
import tikape.sql5.domain.Smoothie;

public class Statistics {
    private Database database;
    
    public Statistics(Database database) {
        this.database = database;
    }
    
    public List<Smoothie> getSmoothies() throws SQLException {
        return new SmoothieDao(database).findAll();
    }
    
    public List<HashMap> getIngredients() throws SQLException {
        return new SmoothieDao(database).howmany();
    }
    
    
}
