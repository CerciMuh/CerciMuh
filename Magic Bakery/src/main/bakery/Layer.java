package bakery;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Layer
 * @author Ali Almuhtaseb

 * @version 1
 */
public class Layer extends Ingredient {
    private final List<Ingredient> recipe;

    @Serial
    final static private long serialVersionUID = 1L;

    /**
     * Initialises a Layer with a given name and recipe
     * @param name Name of the layer
     * @param recipe List of ingredients that make up the Layer
     */
    public Layer(String name, List<Ingredient> recipe) {
        super(name);
        if (recipe == null || recipe.isEmpty()) {
            throw new WrongIngredientsException("Ingredient list can't be empty");
        }
        this.recipe = recipe;
        Collections.sort(recipe);
    }

    /**
     * Checks if the Layer can be baked using the input ingredients
     * @param ingredients List of ingredients
     * @return {@code true} if ingredients include the recipe's required ingredients, {@code false} otherwise
     */
    public boolean canBake(List<Ingredient> ingredients) {
        List<Ingredient> _recipe = new ArrayList<>(recipe);
        for (Ingredient ingredient: ingredients) {
            _recipe.remove(ingredient);
        }
        if (_recipe.isEmpty()) return true;
        return _recipe.size() <= Collections.frequency(ingredients, Ingredient.HELPFUL_DUCK);
    }

    /**
     * Gets the recipe of the Layer
     * @return Recipe of the Layer
     */
    public List<Ingredient> getRecipe() {
        return recipe;
    }

    /**
     * Gets a comma-separated string representation of the Layer's sorted recipe ingredients
     * @return Comma-separated recipe ingredients
     */
    public String getRecipeDescription() {
        return Ingredient.getDescription(recipe);
    }

    /**
     * Gets the hash code, calculated from the recipe list
     * @return Hash code of the layer
     */
    public int hashCode() {
        return recipe.hashCode();
    }
}
