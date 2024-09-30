package util;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.MagicBakery;
import bakery.MagicBakery.ActionType;
import bakery.Player;
/**
 * Utility class for the console
 * @author Ali Almuhtaseb

 * @version 1
 */
public class ConsoleUtils {
    private final Console console;

    /**
     * Initialise ConsoleUtils with the System console
     */
    public ConsoleUtils() {
        console = System.console();
    }

    /**
     * Returns line read from console
     * @return Line read from console
     */
    public String readLine() {
        return console.readLine();
    }

    /**
     * Returns line read from console
     * @param fmt Format string
     * @param args Arguments referenced by the format specifiers in the format string
     * @return Line read from console
     */
    public String readLine(String fmt, Object[] args) {
        return console.readLine(fmt, args);
    }


    /**
     * Prompts the user for a bakery action using a given prompt
     * @param prompt User prompt
     * @param bakery Bakery object
     * @return Action chosen by the user
     */
    public ActionType promptForAction(String prompt, MagicBakery bakery) {
        System.out.println(prompt);
        Collection<Object> options = new ArrayList<>();
        if (!bakery.getPantry().isEmpty()) options.add("Take an ingredient from the pantry");
        if (!bakery.getCurrentPlayer().getHand().isEmpty()) options.add("Give ingredient to another player");
        options.add("Refresh the pantry");
        if (!bakery.getBakeableLayers().isEmpty()) options.add("Bake a layer");
        if (!bakery.getFulfillableCustomers().isEmpty()) options.add("Fulfill a customer order");
        options.add("Save game state");

        Object response = promptEnumerateCollection("Select an option:", options);

        if (response.toString().equals("Take an ingredient from the pantry")) return ActionType.DRAW_INGREDIENT;
        if (response.toString().equals("Give ingredient to another player")) return ActionType.PASS_INGREDIENT;
        if (response.toString().equals("Refresh the pantry")) return ActionType.REFRESH_PANTRY;
        if (response.toString().equals("Bake a layer")) return ActionType.BAKE_LAYER;
        if (response.toString().equals("Fulfill a customer order")) return ActionType.FULFIL_ORDER;
        if (response.toString().equals("Save game state")) return ActionType.SAVE_GAME;
        return null;
    }

    /**
     * Prompts the user for a customer order using a given prompt
     * @param prompt User prompt
     * @param customers List of CustomerOrders
     * @return CustomerOrder chosen by the user
     */
    public CustomerOrder promptForCustomer(String prompt, Collection<CustomerOrder> customers) {
        System.out.println(prompt);
        ArrayList<Object> _customers = new ArrayList<>(customers);
        return (CustomerOrder) promptEnumerateCollection("Select customer:", _customers);
    }

    /**
     * Prompts the user for a player using a given prompt
     * @param prompt User prompt
     * @param bakery Bakery object
     * @return Player chosen by the user
     */
    public Player promptForExistingPlayer(String prompt, MagicBakery bakery) {
        System.out.println(prompt);
        ArrayList<Object> players = new ArrayList<>(bakery.getPlayers());
        players.remove(bakery.getCurrentPlayer());
        return (Player) promptEnumerateCollection("Select player:", players);
    }

    /**
     * Prompts the user for a file path using a given prompt
     * @param prompt User prompt
     * @return File path chosen by the user
     */
    public File promptForFilePath(String prompt) {
        System.out.println(prompt);
        String path = readLine();
        return new File(path);
    }

    /**
     * Prompts the user for an ingredient using a given prompt
     * @param prompt User prompt
     * @param ingredients Collection of ingredients
     * @return Ingredient chosen by the user
     */
    public Ingredient promptForIngredient(String prompt, Collection<Ingredient> ingredients) {
        System.out.println(prompt);
        ArrayList<Object> options = new ArrayList<>(ingredients);
        return (Ingredient) promptEnumerateCollection("Select ingredient:", options);
    }

    /**
     * Prompts the user for new players using a given prompt
     * @param prompt User prompt
     * @return New players chosen by the user
     */
    public List<String> promptForNewPlayers(String prompt) {
        List<String> players = new ArrayList<>();
        console.printf("%s%n", prompt);
        console.printf("Player 1 name? ");
        String player = readLine();
        players.add(player);
        console.printf("Player 2 name? ");
        player = readLine();
        players.add(player);
        for (int i=0; i<3; i++) {
            if(promptForYesNo("Add another?")) {
                console.printf("Player %d name? ", i+3);
                player = readLine();
                players.add(player);
            } else {
                break;
            }
        }
        return players;
    }

    /**
     * Prompts the user for loading a save using a given prompt
     * @param prompt User prompt
     * @return Choice chosen by the user
     */
    public boolean promptForStartLoad(String prompt) {
        while (true) {
            console.printf(prompt + " [S]tart/[L]oad ");
            String answer = readLine();
            if (answer.equals("S") || answer.equals("s")) {
                return true;
            } else if (answer.equals("L") || answer.equals("l")) {
                return false;
            }
            console.printf("Invalid answer" + "\n");
        }
    }

    /**
     * Prompts the user for a yes/no question using a given prompt
     * @param prompt User prompt
     * @return Choice chosen by the user
     */
    public boolean promptForYesNo(String prompt) {
        while (true) {
            console.printf(prompt + " [Y]es/[N]o ");
            String answer = readLine();
            if (answer.equals("Y") || answer.equals("y")) {
                return true;
            } else if (answer.equals("N") || answer.equals("n")) {
                return false;
            }
            console.printf("Invalid answer" + "\n");
        }
    }

    /**
     * Displays a numbered list of options in a collection
     * @param prompt User prompt
     * @param collection Collection options to display
     * @return Choice chosen by the user
     */
    private Object promptEnumerateCollection(String prompt, Collection<Object> collection) {
        if (collection == null || collection.isEmpty())
            throw new IllegalArgumentException();
        System.out.println(prompt);
        ArrayList<Object> _collection = new ArrayList<>(collection);
        for (int i=0; i<_collection.size(); i++) {
            System.out.printf("[%d] %s\n", i, _collection.get(i).toString());
        }
        while (true) {
            String answer = readLine();
            if (Integer.parseInt(answer) > -1  && Integer.parseInt(answer) < collection.size()) {
                return _collection.get(Integer.parseInt(answer));
            } else {
                System.out.println("Invalid answer");
            }
        }
    }
}
