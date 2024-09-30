package bakery;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.StringUtils;

/**
 * Represents a Player
 * @author Ali Almuhtaseb

 * @version 1
 */
public class Player implements Serializable {
    private final List<Ingredient> hand;
    private final String name;

    @Serial
    final private static long serialVersionUID = 1L;

    /**
     * Initialises a Player with a given name and an empty hand
     * @param name Name of the player
     */
    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
    }

    /**
     * Adds a list of ingredients to the player's hand and sorts the new list
     * @param ingredients The ingredients to add to the player's hand
     */
    public void addToHand(List<Ingredient> ingredients) {
        hand.addAll(ingredients);
        Collections.sort(hand);
    }

    /**
     * Add a single ingredient to the player's hand and sorts the new list
     * @param ingredient The ingredient to add to the player's hand
     */
    public void addToHand(Ingredient ingredient) {
        hand.add(ingredient);
        Collections.sort(hand);
    }

    /**
     * Check if the player's hand contains an ingredient
     * @param ingredient Ingredient to check
     * @return true if player's hand contains ingredient
     */
    public boolean hasIngredient(Ingredient ingredient) {
        return hand.contains(ingredient);
    }

    /**
     * Removes an ingredient from the player's hand
     * @param ingredient Ingredient to remove
     */
    public void removeFromHand(Ingredient ingredient) {
        if (!hand.remove(ingredient)) {
            throw new WrongIngredientsException("Ingredients list doesn't contain ingredient");
        }
    }

    /**
     * Gets the current ingredients in the player's hand
     * @return List of ingredients
     */
    public List<Ingredient> getHand() {
        return hand;
    }

    /**
     * Gets a string representation of the ingredients in the player's hand
     * @return Comma-separated string of ingredient names
     */
    public String getHandStr() {
        if (hand.isEmpty()) return "";
        Collections.sort(hand);
        Ingredient.getDescription(hand);
        String getHandStr = "";
        int index = 0;
        int counter = 1;
        while (index < hand.size() - 1) {
            if (hand.get(index) == hand.get(index+1)) {
                counter++;
            } else {
                if (counter > 1) {
                    getHandStr = getHandStr
                            .concat(StringUtils.toTitleCase(hand.get(index).getName()))
                            .concat(" (x")
                            .concat(Integer.toString(counter))
                            .concat("), ");
                } else {
                    getHandStr = getHandStr
                            .concat(StringUtils.toTitleCase(hand.get(index).getName())).concat(", ");
                }
                counter = 1;
            }
            index++;
        }
        if (counter > 1) {
            getHandStr = getHandStr
                    .concat(StringUtils.toTitleCase(hand.get(index).getName()))
                    .concat(" (x")
                    .concat(Integer.toString(counter))
                    .concat("), ");
        } else {
            getHandStr = getHandStr
                    .concat(StringUtils.toTitleCase(hand.get(index).getName()))
                    .concat(", ");
        }
        return getHandStr.substring(0, getHandStr.length()-2);
    }

    /**
     * Gets the player name
     * @return Player name
     */
    public String toString() {
        return name;
    }
}
