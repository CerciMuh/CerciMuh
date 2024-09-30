package bakery;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Ingredient
 * @author Ali Almuhtaseb

 * @version 1
 */
public class Ingredient implements Comparable<Ingredient>, Serializable {
    private final String name;
    /**
     * Helpful duck which can replace any ingredient
     */
    static final public Ingredient HELPFUL_DUCK = new Ingredient("helpful duck \uD80C\uDD6D");

    @Serial
    static final private long serialVersionUID = 1L;

    /**
     * Initialises an ingredient with a given name
     * @param name Name of the ingredient
     */
    public Ingredient(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the ingredient
     * @return Name of the ingredient
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a comma-separated list of ingredient names
     * @param ingredients List of ingredients
     * @return Comma-separated list of ingredient names
     */
    public static String getDescription(List<Ingredient> ingredients) {
        List<String> ingredientNames = new ArrayList<>();
        for (Ingredient ingredient: ingredients) {
            ingredientNames.add(ingredient.getName());
        }
        return String.join(", ", ingredientNames);
    }

    /**
     * Checks if a given object is equal to the ingredient
     * @param obj Reference object to compare with
     * @return {@code true} if {@code obj} is the same as ingredient, {@code false} otherwise
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof Ingredient)) {
            return false;
        }

        return name.equals(((Ingredient) obj).getName());
    }

    /**
     * Gets the hash code, calculated from the ingredient name
     * @return Hash code of the ingredient
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Gets the name of the ingredient
     * @return Name of the ingredient
     */
    public String toString() {
        return name;
    }

    /**
     * Compares two ingredients by ASCII values of their names
     * @param i the ingredient to be compared.
     * @return 0 if the ingredient names are the same,
     * a value less than 0 if the ingredient name is lower than the input ingredient name,
     * a value greater than 0 if the ingredient name is higher than the input ingredient name,
     */
    public int compareTo(Ingredient i) {
        return name.compareTo(i.name);
    }
}
