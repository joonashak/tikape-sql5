package tikape.runko.domain;

/**
 *
 * @author Joonas
 */
public class Smoothie {
    private static int id;
    private static String name;

    public Smoothie(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Smoothie.id = id;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Smoothie.name = name;
    }

    
}
