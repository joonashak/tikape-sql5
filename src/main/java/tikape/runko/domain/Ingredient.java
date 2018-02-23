package tikape.runko.domain;

/**
 *
 * @author Joonas
 */
public class Ingredient {
    int id;
    String name;

    public Ingredient(int id, String name) {
        this.id = id;
        this.name = name;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
