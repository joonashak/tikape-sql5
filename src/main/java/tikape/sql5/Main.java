package tikape.sql5;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Spark;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.sql5.database.Database;
import tikape.sql5.database.IngredientDao;
import tikape.sql5.database.SmoothieDao;
import tikape.sql5.domain.Ingredient;
import tikape.sql5.domain.Smoothie;
import static tikape.sql5.ui.ModelAndView.createView;

public class Main {

    public static void main(String[] args) throws Exception {
        
        Spark.staticFileLocation("/public");
        
        Database database = new Database();
        SmoothieDao smoothieDao = new SmoothieDao(database);
        IngredientDao ingredientDao = new IngredientDao(database);

        get("/", (req, res) -> {
            HashMap model = new HashMap<>();

            // TODO
            return createView("ingredients", model);
        }, new ThymeleafTemplateEngine());
        
        get("/smoothies", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("smoothies", smoothieDao.findAll());
            model.put("ingredients", ingredientDao.findAll());

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie/:id", (req, res) -> {
            Integer key = Integer.parseInt(req.params("id"));
            HashMap model = new HashMap<>();
            
            model.put("smoothie", smoothieDao.findOne(key));

            return new ModelAndView(model, "smoothie");
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie/delete/:id", (req, res) -> {
            smoothieDao.delete(Integer.parseInt(req.params("id")));
            
            HashMap model = new HashMap<>();
            model.put("smoothies", smoothieDao.findAll());
            model.put("ingredients", ingredientDao.findAll());

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        post("/smoothie", (req, res) -> {
            HashMap model = new HashMap<>();
            
            Smoothie smoothie = new Smoothie(0, req.queryParams("name"));
            smoothieDao.save(smoothie);
            
            res.redirect("/smoothies", 303);
            
            return new ModelAndView(model, "index");
        }, new ThymeleafTemplateEngine());
        
        get("/ingredients", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("ingredients", ingredientDao.findAll());

            return createView("ingredients", model);
        }, new ThymeleafTemplateEngine());
        
        get("/ingredient/delete/:id", (req, res) -> {
            HashMap model = new HashMap<>();
            ingredientDao.delete(Integer.parseInt(req.params("id")));
            
            res.redirect("/ingredients", 303);
            
            return new ModelAndView(model, "index");
        }, new ThymeleafTemplateEngine());
        
        // Add new ingredient to db
        post("/ingredient", (req, res) -> {
            HashMap model = new HashMap<>();
            
            Ingredient ingredient = new Ingredient(0, req.queryParams("name"), "", "");
            ingredientDao.save(ingredient);
            
            res.redirect("/ingredients", 303);
            
            return new ModelAndView(model, "index");
        }, new ThymeleafTemplateEngine());
        
        // Add an (existing) ingredient to a smoothie
        post("/ingredient/add", (req, res) -> {
            HashMap model = new HashMap<>();
            int smoothieId = Integer.parseInt(req.queryParams("smoothie"));
            int ingredientId = Integer.parseInt(req.queryParams("ingredient"));
            int order = Integer.parseInt(req.queryParams("order"));
            String amount = req.queryParams("amount");
            String info = req.queryParams("info");
            
            Ingredient ingredient = ingredientDao.findOne(ingredientId);
            ingredient.setAmount(amount);
            ingredient.setInfo(info);
            
            smoothieDao.addIngredient(smoothieId, ingredient, order);
            
            String redirectUrl = "/smoothie/" + smoothieId;
            res.redirect(redirectUrl, 303);

            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
    }
}
