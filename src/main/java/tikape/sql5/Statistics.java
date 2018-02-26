package tikape.sql5;

import java.sql.SQLException;
import java.util.List;
import tikape.sql5.database.Database;
import tikape.sql5.database.IngredientDao;
import tikape.sql5.database.SmoothieDao;
import tikape.sql5.domain.Ingredient;
import tikape.sql5.domain.Smoothie;

public class Statistics {
    private Database database;
    private String testi = "Hei";

    public Statistics(Database database) {
        this.database = database;
    }

    public String getTest() {
        return testi;
    }
    
    public Integer getCount(Integer id) throws SQLException{
        SmoothieDao smoothieDao = new SmoothieDao(database);
        Integer n = smoothieDao.howmany(id);
        return n;
    }
    
    public List<Smoothie> getSmoothies() throws SQLException {
        return new SmoothieDao(database).findAll();
    }
    
    public List<Ingredient> getIngredients() throws SQLException {
        return new IngredientDao(database).findAll();
    }
    
    
}
