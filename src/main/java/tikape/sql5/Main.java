package tikape.sql5;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.sql5.database.Database;
import tikape.sql5.database.IngredientDao;
import tikape.sql5.database.SmoothieDao;
import tikape.sql5.domain.Ingredient;
import tikape.sql5.domain.Smoothie;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database();

        get("/", (req, res) -> {
            HashMap model = new HashMap<>();

            return new ModelAndView(model, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("smoothies", new SmoothieDao(database).findAll());
            model.put("ingredients", new IngredientDao(database).findAll());

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie/:id", (req, res) -> {
            Integer key = Integer.parseInt(req.params("id"));
            HashMap model = new HashMap<>();
            
            model.put("smoothie", new SmoothieDao(database).findOne(key));

            return new ModelAndView(model, "smoothie");
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie/delete/:id", (req, res) -> {
            new SmoothieDao(database).delete(Integer.parseInt(req.params("id")));
            
            HashMap model = new HashMap<>();
            model.put("smoothies", new SmoothieDao(database).findAll());
            model.put("ingredients", new IngredientDao(database).findAll());

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        post("/smoothie", (req, res) -> {
            HashMap model = new HashMap<>();
            
            Smoothie smoothie = new Smoothie(0, req.queryParams("name"));
            new SmoothieDao(database).save(smoothie);
            
            res.redirect("/smoothie", 303);
            
            return new ModelAndView(model, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/ingredient", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("viesti", "tervehdys");

            return new ModelAndView(model, "ingredients");
        }, new ThymeleafTemplateEngine());
        
        get("/ingredient/delete/:id", (req, res) -> {
            new IngredientDao(database).delete(Integer.parseInt(req.params("id")));
            
            HashMap model = new HashMap<>();
            model.put("smoothies", new SmoothieDao(database).findAll());
            model.put("ingredients", new IngredientDao(database).findAll());

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        // Add new ingredient to db
        post("/ingredient", (req, res) -> {
            Ingredient ingredient = new Ingredient(0, req.queryParams("name"), "", "");
            new IngredientDao(database).save(ingredient);
            
            HashMap model = new HashMap<>();
            model.put("smoothies", new SmoothieDao(database).findAll());
            model.put("ingredients", new IngredientDao(database).findAll());

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        // Add an (existing) ingredient to a smoothie
        post("/ingredient/add", (req, res) -> {
            HashMap model = new HashMap<>();
            int smoothieId = Integer.parseInt(req.queryParams("smoothie"));
            int ingredientId = Integer.parseInt(req.queryParams("ingredient"));
            int order = Integer.parseInt(req.queryParams("order"));
            String amount = req.queryParams("amount");
            String info = req.queryParams("info");
            
            Ingredient ingredient = 
                    new IngredientDao(database).findOne(ingredientId);
            ingredient.setAmount(amount);
            ingredient.setInfo(info);
            
            new SmoothieDao(database)
                    .addIngredient(smoothieId, ingredient, order);
            
            String redirectUrl = "/smoothie/" + smoothieId;
            res.redirect(redirectUrl, 303);

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
    }
}
