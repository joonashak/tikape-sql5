package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.domain.Ingredient;
import tikape.runko.domain.Smoothie;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database();
        
        List<Smoothie> smoothies = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viesti", "tervehdys");

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("/smoothies", (req, res) -> {
////            SMOOTHIEN LISÄÄMINEN
            

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("ingredients/", (req, res) -> {
//            RAAKA-AINEEN LISÄÄMINEN


            return null;
        }, new ThymeleafTemplateEngine());
        
        post("ingredients/add", (req, res) -> {
//            UUDEN (RAAKA-AINE) RIVIN LISÄÄMINEN RESEPTIIN
            

            return null;
        }, new ThymeleafTemplateEngine());
        

    }
}
