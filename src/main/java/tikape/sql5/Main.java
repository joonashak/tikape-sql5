package tikape.sql5;

import java.sql.SQLException;
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
        
        if (System.getenv("PORT") != null) {
            int port = Integer.parseInt(System.getenv("PORT"));
            Spark.port(port);
        }
        
        Spark.staticFileLocation("/public");
        
        Database database = new Database();
        SmoothieDao smoothieDao = new SmoothieDao(database);
        IngredientDao ingredientDao = new IngredientDao(database);

        get("/", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("smoothies", smoothieDao.findAll());

            return createView("smoothies", model);
        }, new ThymeleafTemplateEngine());
        
        get("/smoothies", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("smoothies", smoothieDao.findAll());
            
            if (req.queryParams("delete") != null) {
                model.put("deleteSmoothieSuccess", true);
            }

            return createView("smoothies", model);
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie/:id", (req, res) -> {
            Integer key = Integer.parseInt(req.params("id"));
            HashMap model = new HashMap<>();
            Smoothie smoothie = smoothieDao.findOne(key);
            
            model.put("smoothie", smoothie);
            model.put("ingredients", ingredientDao.findAll());
            model.put("nextRow", smoothie.getIngredients().size() + 1);
            
            if (req.queryParams("new") != null) {
                model.put("newSmoothieSuccess", true);
            }

            return createView("smoothie", model);
        }, new ThymeleafTemplateEngine());
        
        get("/smoothie/delete/:id", (req, res) -> {
            smoothieDao.delete(Integer.parseInt(req.params("id")));
            
            HashMap model = new HashMap<>();
            model.put("smoothies", smoothieDao.findAll());
            model.put("ingredients", ingredientDao.findAll());

            res.redirect("/smoothies?delete=true", 303);
            
            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        post("/smoothie", (req, res) -> {
            HashMap model = new HashMap<>();
            
            Smoothie smoothie = new Smoothie(0, req.queryParams("name"));
            smoothie = smoothieDao.save(smoothie);
            
            res.redirect("/smoothie/" + smoothie.getId() + "?new=true", 303);
            
            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        post("/smoothie/row", (req, res) -> {
            HashMap model = new HashMap<>();
            int smoothieId = Integer.parseInt(req.queryParams("smoothie_id"));
            Ingredient ingredient = new Ingredient();

            if (req.queryParams("ingredient_id") == null) {
                ingredient.setName(req.queryParams("ingredient_name"));
                ingredient = ingredientDao.save(ingredient);
            } else {
                Integer integerId = 
                        Integer.parseInt(req.queryParams("ingredient_id"));
                ingredient = ingredientDao.findOne(integerId);
            }
            
            int order = Integer.parseInt(req.queryParams("order"));
            String amount = req.queryParams("amount");
            String info = req.queryParams("info");
            
            ingredient.setAmount(amount);
            ingredient.setInfo(info);
            
            smoothieDao.addIngredient(smoothieId, ingredient, order);
            
            res.redirect("/smoothie/" + smoothieId, 303);
            return createView("/smoothies", model);
        }, new ThymeleafTemplateEngine());
        
        get("/ingredients", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("ingredients", ingredientDao.findAll());

            if (req.queryParams("error") != null) {
                model.put("deleteIngredientError", true);
            } else if (req.queryParams("delete") != null) {
                model.put("deleteIngredientSuccess", true);
            }
            
            return createView("ingredients", model);
        }, new ThymeleafTemplateEngine());
        
        get("/ingredient/delete/:id", (req, res) -> {
            HashMap model = new HashMap<>();
            String redirectUrl = "/ingredients";
            
            try {
                ingredientDao.delete(Integer.parseInt(req.params("id")));
                redirectUrl += "?delete=true";
            } catch (SQLException e) {
                redirectUrl += "?error=true";
            }
            
            res.redirect(redirectUrl, 303);
            
            return new ModelAndView(model, "smoothies");
        }, new ThymeleafTemplateEngine());
        
        get("/stats", (req, res) -> {
            HashMap model = new HashMap<>();
            model.put("stats", new Statistics(database));
            
            return createView("statistics", model);
        }, new ThymeleafTemplateEngine());
        
        // Add new ingredient to db
        post("/ingredient", (req, res) -> {
            HashMap model = new HashMap<>();
            
            Ingredient ingredient = new Ingredient(0, req.queryParams("name"), "", "");
            ingredientDao.save(ingredient);
            
            res.redirect("/ingredients", 303);
            
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
