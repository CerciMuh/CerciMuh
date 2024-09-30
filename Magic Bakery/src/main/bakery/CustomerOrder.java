package bakery;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a CustomerOrder
 * @author Ali Almuhtaseb
 * @version 1
 */
public class CustomerOrder implements Serializable {
    private final List<Ingredient> garnish;
    private final int level;
    private final String name;
    private final List<Ingredient> recipe;
    private CustomerOrderStatus status;

    @Serial
    final private static long serialVersionUID = 1L;

    /**
     * Constructor for CustomerOrder
     * <br/>
     * Every order initially has a status of WAITING
     * @param name Customer name
     * @param recipe Customer recipe
     * @param garnish Customer garnish
     * @param level Customer level
     */
    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level) {
        if (recipe == null || garnish == null || recipe.isEmpty())
            throw new WrongIngredientsException("Recipe list can't be empty");
        this.name = name;
        this.level = level;
        this.recipe = recipe;
        this.garnish = garnish;
        this.status = CustomerOrderStatus.WAITING;
    }

    /**
     * Status options for the CustomerOrder
     */
    public enum CustomerOrderStatus {
        WAITING,
        FULFILLED,
        GARNISHED,
        IMPATIENT,
        GIVEN_UP
    }

    /**
     * Abandons the customer's order and sets the status to {@code GIVEN_UP}
     */
    public void abandon() {
        status = CustomerOrderStatus.GIVEN_UP;
    }

    /**
     * Checks if the input ingredients contain the recipe ingredients
     * @param ingredients List of ingredients
     * @return {@code true} if ingredients include the recipe's required ingredients, {@code false} otherwise
     */
    public boolean canFulfill(List<Ingredient> ingredients) {
        List<Ingredient> _recipe = new ArrayList<>(recipe);
        for (Ingredient ingredient: ingredients) {
            _recipe.remove(ingredient);
        }
        if (_recipe.isEmpty()) return true;
        for (Ingredient _ingredient: _recipe) {
            if (_ingredient instanceof Layer) return false;
        }
        return _recipe.size() <= Collections.frequency(ingredients, Ingredient.HELPFUL_DUCK);
    }

    /**
     * Checks if the input ingredients contain the garnish ingredients
     * @param ingredients List of ingredients
     * @return {@code true} if ingredients include the garnish's required ingredients, {@code false} otherwise
     */
    public boolean canGarnish(List<Ingredient> ingredients) {
        List<Ingredient> _garnish = new ArrayList<>(garnish);
        for (Ingredient ingredient: ingredients) {
            _garnish.remove(ingredient);
        }
        if (_garnish.isEmpty()) return true;
        for (Ingredient _ingredient: _garnish) {
            if (_ingredient instanceof Layer) return false;
        }
        return _garnish.size() <= Collections.frequency(ingredients, Ingredient.HELPFUL_DUCK);
    }

    /**
     * Fulfills the customer's order using the given ingredient list, with or without garnish
     * @param ingredients List of ingredients
     * @param garnish Flag to choose to fulfill with or without garnish
     * @return List of ingredients used to fulfill the order
     */
    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish) {
        List<Ingredient> ingredientList = new ArrayList<>(ingredients);
        List<Ingredient> used = new ArrayList<>();
        for (Ingredient ingredient: recipe) {
            if (!ingredientList.remove(ingredient)) {
                if (ingredient instanceof Layer)
                    throw new WrongIngredientsException("Missing layer ingredient");
                if (!ingredientList.remove(Ingredient.HELPFUL_DUCK)) {
                    throw new WrongIngredientsException("Missing ingredient");
                }
                used.add(Ingredient.HELPFUL_DUCK);
            } else {
                used.add(ingredient);
            }
        }
        status = CustomerOrderStatus.FULFILLED;

        if (!garnish || this.garnish.isEmpty() || !canGarnish(ingredientList)) {
            return used;
        }
        for (Ingredient ingredient: this.garnish) {
            if (!ingredientList.remove(ingredient)) {
                ingredientList.remove(Ingredient.HELPFUL_DUCK);
                used.add(Ingredient.HELPFUL_DUCK);
            } else {
                used.add(ingredient);
            };
        }
        status = CustomerOrderStatus.GARNISHED;
        return used;
    }

    /**
     * Gets the garnish of the CustomerOrder
     * @return Garnish of the CustomerOrder
     */
    public List<Ingredient> getGarnish() {
        return garnish;
    }

    /**
     * Gets a comma-separated string representation of the CustomerOrder's sorted garnish ingredients
     * @return Comma-separated garnish ingredients
     */
    public String getGarnishDescription() {
        return Ingredient.getDescription(garnish);
    }

    /**
     * Gets the level of the CustomerOrder
     * @return Level of the CustomerOrder
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the recipe of the CustomerOrder
     * @return Recipe of the CustomerOrder
     */
    public List<Ingredient> getRecipe() {
        return recipe;
    }

    /**
     * Gets a comma-separated string representation of the CustomerOrder's sorted recipe ingredients
     * @return Comma-separated recipe ingredients
     */
    public String getRecipeDescription() {
        return Ingredient.getDescription(recipe);
    }

    /**
     * Gets the current status of the CustomerOrder
     * @return Current status of the CustomerOrder
     */
    public CustomerOrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the CustomerOrder status
     * @param status The status to set
     */
    public void setStatus(CustomerOrderStatus status) {
        this.status = status;
    }

    /**
     * Gets the customer name
     * @return Customer name
     */
    public String toString() {
        return name;
    }
}
