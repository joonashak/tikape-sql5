package tikape.sql5.domain;

/**
 *
 * @author Joonas
 */
public class Ingredient {
    int id;
    String name;
    String amount = "";
    String info = "";

    public Ingredient() {
    }
    
    public Ingredient(String name) {
        this.name = name;
    }

    public Ingredient(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Ingredient(int id, String name, String amount, String info) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.info = info;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    
}
