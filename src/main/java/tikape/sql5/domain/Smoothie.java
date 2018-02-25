package tikape.sql5.domain;

import java.util.List;

public class Smoothie {
    
    private int id;
    private String name;
    private List<Ingredient> ingredients;

    public Smoothie(String name) {
        this.name = name;
    }

    public Smoothie(int id, String name) {
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

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
    
    
}
