package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;

/**
 * Utility class for cards
 * @author Ali Almuhtaseb

 * @version 1
 */
public class CardUtils {
    private CardUtils() {};

    /**
     * Returns list of customer orders in a file
     * @param path Path to customer orders file
     * @param layers List of layers
     * @return List of customer orders
     * @throws FileNotFoundException If customer orders file not found
     */
    public static List<CustomerOrder> readCustomerFile(String path, Collection<Layer> layers) throws FileNotFoundException {
        List<CustomerOrder> customerOrders = new ArrayList<>();
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (!line.equals("LEVEL, NAME, RECIPE, GARNISH"))
                    customerOrders.add(stringToCustomerOrder(line, layers));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found");
        }
        return customerOrders;
    }

    /**
     * Gets a parsed list of ingredients from the source file
     * @param path Path of the source file
     * @return List of parsed ingredients
     * @throws FileNotFoundException If ingredients file not found
     */
    public static List<Ingredient> readIngredientFile(String path) throws FileNotFoundException {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (!line.equals("NAME, COUNT"))
                    ingredients.addAll(stringToIngredients(line));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found");
        }
        return ingredients;
    }

    /**
     * Gets a parsed list of layers from the source file
     * @param path Path of the source file
     * @return List of parsed layers
     * @throws FileNotFoundException If layers file not found
     */
    public static List<Layer> readLayerFile(String path) throws FileNotFoundException {
        List<Layer> layers = new ArrayList<>();
        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (!line.equals("NAME, RECIPE"))
                    layers.addAll(stringToLayers(line));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found");
        }
        return layers;
    }

    private static Layer findLayer(String str, Collection<Layer> layers) {
        for (Layer layer: layers) {
            if (layer.getName().equals(str)) {
                return layer;
            }
        }
        return null;
    }

    /**
     * Converts a string to a customer order
     * @param str Comma-separated customer order
     * @param layers List of layers
     * @return CustomerOrder
     */
    private static CustomerOrder stringToCustomerOrder(String str, Collection<Layer> layers) {
        String[] parts = str.split(",");
        int level = Integer.parseInt(parts[0].strip());
        String name = parts[1].strip();
        String recipeParts = parts[2].strip();

        String[] recipeIngredients = recipeParts.split(";");
        List<Ingredient> recipe = new ArrayList<>();
        for (String recipeIngredient: recipeIngredients) {
            Layer recipeLayer = findLayer(recipeIngredient, layers);
            if (recipeLayer != null) {
                recipe.add(new Layer(recipeIngredient.strip(), recipeLayer.getRecipe()));
            } else {
                recipe.add(new Ingredient(recipeIngredient.strip()));
            }
        }

        List<Ingredient> garnish = new ArrayList<>();
        if (parts.length > 3) {
            String garnishParts = parts[3].strip();
            String[] garnishIngredients = garnishParts.split(";");
            garnish = new ArrayList<>();
            for (String garnishIngredient: garnishIngredients) {
                Layer recipeLayer = findLayer(garnishIngredient, layers);
                if (recipeLayer != null) {
                    garnish.add(new Layer(garnishIngredient.strip(), recipeLayer.getRecipe()));
                } else {
                    garnish.add(new Ingredient(garnishIngredient.strip()));
                }
            }
        }

        return new CustomerOrder(name, recipe, garnish, level);
    }

    /**
     * Returns a list of the parsed ingredient repeated by its count number
     * @param str Comma-separated ingredient name and count
     * @return List of ingredients
     */
    private static List<Ingredient> stringToIngredients(String str) {
        String[] parts = str.split(",");
        String name = parts[0].strip();
        int count = Integer.parseInt(parts[1].strip());
        Ingredient ingredient = new Ingredient(name);
        return new ArrayList<>(Collections.nCopies(count, ingredient));
    }

    /**
     * Returns a list containing the parsed layer
     * @param str Comma-separated layer name and semicolon-separated ingredients
     * @return List of layers
     */
    private static List<Layer> stringToLayers(String str) {
        String[] parts = str.split(",");
        String name = parts[0].strip();
        String recipeIngredients = parts[1].strip();

        List<Ingredient> recipe = new ArrayList<>();
        String[] ingredients = recipeIngredients.split(";");
        for (String ingredient: ingredients) {
            recipe.add(new Ingredient(ingredient.strip()));
        }

        Layer layer = new Layer(name, recipe);
        List<Layer> layers = new ArrayList<>();
        layers.add(layer);
        layers.add(layer);
        layers.add(layer);
        layers.add(layer);
        return layers;
    }
}
