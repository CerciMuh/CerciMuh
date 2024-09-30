package bakery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import util.CardUtils;
import util.ConsoleUtils;
import util.StringUtils;

/**
 * Represents a MagicBakery
 * @author Ali Almuhtaseb

 * @version 1
 */
public class MagicBakery implements Serializable {
    private Customers customers;
    private final Collection<Layer> layers;
    private final Collection<Player> players;
    private Player currentPlayer;
    private int playerActionsRemaining;
    private final Collection<Ingredient> pantry;
    private final Collection<Ingredient> pantryDeck;
    private final Collection<Ingredient> pantryDiscard;
    private final Random random;
    private final transient ConsoleUtils consoleUtils;

    @Serial
    final private static long serialVersionUID = 1L;

    /**
     * Magic Bakery Constructor
     * @param seed Random seed to use throughout the game
     * @param ingredientDeckFile Path to ingredient file
     * @param layerDeckFile Path to layer file
     * @throws FileNotFoundException If ingredient or layer file not found
     */
    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) throws FileNotFoundException {
        pantryDeck = CardUtils.readIngredientFile(ingredientDeckFile);
        layers = CardUtils.readLayerFile(layerDeckFile);
        pantry = new ArrayList<>();
        pantryDiscard = new ArrayList<>();
        players = new ArrayList<>();
        random = new Random(seed);
        consoleUtils = new ConsoleUtils();
    }

    /**
     * Actions a player can take
     */
    public enum ActionType {
        DRAW_INGREDIENT,
        PASS_INGREDIENT,
        BAKE_LAYER,
        FULFIL_ORDER,
        REFRESH_PANTRY,
        SAVE_GAME
    }

    /**
     * Bakes a layer using ingredients from the current player's hand
     * @param layer Layer to bake
     */
    public void bakeLayer(Layer layer) {
        if (playerActionsRemaining == 0)
            throw new TooManyActionsException();
        if (!layer.canBake(currentPlayer.getHand()))
            throw new WrongIngredientsException("Layer can't be baked with given ingredients");
        playerActionsRemaining -= 1;
        for (Ingredient ingredient: layer.getRecipe()) {
            if (currentPlayer.hasIngredient(ingredient)) {
                currentPlayer.removeFromHand(ingredient);
                pantryDiscard.add(ingredient);
            }
            else {
                currentPlayer.removeFromHand(Ingredient.HELPFUL_DUCK);
                pantryDiscard.add(Ingredient.HELPFUL_DUCK);
            }
        }
        layers.remove(layer);
        currentPlayer.addToHand(layer);
    }

    /**
     * Completes a specific action
     * @param action Action to complete
     * @throws IOException If file write fails
     */
    public void doAction(ActionType action) throws IOException {
        switch (action) {
            case REFRESH_PANTRY -> refreshPantry();
            case BAKE_LAYER -> bakeLayer((Layer) consoleUtils.promptForIngredient("Select layer: ", new ArrayList<>(getBakeableLayers())));
            case FULFIL_ORDER -> {
                CustomerOrder customerOrder = consoleUtils.promptForCustomer("Select order to fulfill: ", getFulfillableCustomers());
                boolean willGarnish = false;
                if (customerOrder.canGarnish(currentPlayer.getHand())) {
                    willGarnish = consoleUtils.promptForYesNo("With garnish?");
                }
                fulfillOrder(customerOrder, willGarnish);
            }
            case DRAW_INGREDIENT -> {
                if (consoleUtils.promptForYesNo("Draw from current pantry ingredients (Yes), or top card from deck (No)?")) {
                    drawFromPantry(consoleUtils.promptForIngredient("Select pantry ingredient:", pantry));
                } else {
                    drawFromPantryDeck();
                }
            }
            case PASS_INGREDIENT -> passCard(consoleUtils.promptForIngredient("Select ingredient to pass:", currentPlayer.getHand()), consoleUtils.promptForExistingPlayer("Select player to pass card to:", this));
            case SAVE_GAME -> saveState(consoleUtils.promptForFilePath("Enter path to save game state:"));
        }
    }

    /**
     * Moves the topmost ingredient in the pantry deck to the player's hand
     * @return the topmost ingredient in the pantry deck
     */
    private Ingredient drawFromPantryDeck() {
        if (playerActionsRemaining == 0)
            throw new TooManyActionsException();
        Ingredient newCard = getCardFromDeck();
        currentPlayer.addToHand(newCard);
        return newCard;
    }

    /**
     Draws a card from the pantry to the current player's hand
     * @param ingredientName Ingredient name to draw from the pantry
     */
    public void drawFromPantry(String ingredientName) {
        Ingredient ingredient = new Ingredient(ingredientName);
        drawFromPantry(ingredient);
    }

    /**
     * Draws a card from the pantry to the current player's hand
     * @param ingredient Ingredient to draw from the pantry
     */
    public void drawFromPantry(Ingredient ingredient) {
        if (playerActionsRemaining == 0)
            throw new TooManyActionsException();
        boolean removed = pantry.remove(ingredient);
        if (!removed) {
            throw new WrongIngredientsException("Ingredient isn't in pantry");
        }
        playerActionsRemaining -= 1;
        currentPlayer.addToHand(ingredient);
        Ingredient newCard = getCardFromDeck();
        if (newCard != null) {
            pantry.add(newCard);
        }
    }

    /**
     * Gets the top card from the pantry deck
     * @return Top card from the pantry deck
     */
    private Ingredient getCardFromDeck() {
        try {
            return ((ArrayList<Ingredient>)pantryDeck).remove(pantryDeck.size()-1);
        } catch (EmptyPantryException | IndexOutOfBoundsException e) {
            if (pantryDiscard.isEmpty())
                throw new EmptyPantryException("Pantry deck and discard are both empty", e);
            pantryDeck.addAll(pantryDiscard);
            pantryDiscard.clear();
            Collections.shuffle((ArrayList<Ingredient>)pantryDeck, random);
            return ((ArrayList<Ingredient>)pantryDeck).remove(pantryDeck.size()-1);
        }
    }

    /**
     * Ends the current player's turn
     * @return {@code true} if the game has ended
     */
    public boolean endTurn() {
        playerActionsRemaining = getActionsPermitted();
        List<Player> playerList = (ArrayList<Player>) players;
        int index = playerList.indexOf(currentPlayer);
        currentPlayer = playerList.get((index+1)%playerList.size());
        if (index==playerList.size()-1) {
            try {
                customers.addCustomerOrder();
            } catch (EmptyStackException ignored) {

            }
        }
        ArrayList<CustomerOrder> _customers = new ArrayList<>(customers.getActiveCustomers());
        boolean isActiveCustomersEmpty = _customers.get(0) == null && _customers.get(1) == null && _customers.get(2) == null;
        return isActiveCustomersEmpty && customers.getCustomerDeck().isEmpty();
    }

    /**
     * Fulfills a customer order using the player's cards
     * @param customer Customer order to fulfill
     * @param garnish Fulfill including the garnish or not
     * @return Cards drawn as a result of garnishing an order
     */
    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish) {
        if (playerActionsRemaining == 0)
            throw new TooManyActionsException();
        playerActionsRemaining -= 1;
        List<Ingredient> used = customer.fulfill(currentPlayer.getHand(), garnish);
        for (Ingredient usedIngredient: used) {
            currentPlayer.removeFromHand(usedIngredient);
            if (usedIngredient instanceof Layer) {
                layers.add((Layer) usedIngredient);
            } else {
                pantryDiscard.add(usedIngredient);
            }
        }
        customer.setStatus(CustomerOrder.CustomerOrderStatus.FULFILLED);
        if (garnish) {
            currentPlayer.addToHand(customer.getGarnish());
            customer.setStatus(CustomerOrder.CustomerOrderStatus.GARNISHED);
        }
        customers.remove(customer);
        customers.setImpatient();
        if (garnish) {
            return customer.getGarnish();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns number of permitted actions per turn based on number of players
     * @return Number of permitted actions
     */
    public int getActionsPermitted() {
        int numOfPlayers = players.size();
        if (numOfPlayers == 2 || numOfPlayers == 3) {
            return 3;
        }
        if (numOfPlayers == 4 || numOfPlayers == 5) {
            return 2;
        }
        return 0;
    }

    /**
     * returns the remaining number of actions for the current player
     * @return the remaining number of actions for the current player
     */
    public int getActionsRemaining() {
        return playerActionsRemaining;
    }

    /**
     * Returns layers than can be baked using ingredients from the current player's hand
     * @return layers than can be baked using ingredients from the current player's hand
     */
    public Collection<Layer> getBakeableLayers() {
        List<Layer> _layers = getLayers().stream().toList();
        Collection<Layer> bakeableLayers = new ArrayList<>();
        for (Layer layer: _layers) {
            if (layer.canBake(currentPlayer.getHand())){
                bakeableLayers.add(layer);
            }
        }
        return bakeableLayers;
    }

    /**
     * Returns the currently active player
     * @return the currently active player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the current Customers
     * @return Customers collection
     */
    public Customers getCustomers() {
        return customers;
    }

    /**
     * Returns customers than can be fulfilled using the player's hand
     * @return customers than can be fulfilled using the player's hand
     */
    public Collection<CustomerOrder> getFulfillableCustomers() {
        List<CustomerOrder> customerOrders = customers.getActiveCustomers().stream().toList();
        Collection<CustomerOrder> fulfillableCustomers = new ArrayList<>();
        for (int i=0; i<3; i++) {
            CustomerOrder customerOrder = customerOrders.get(i);
            if (customerOrder != null && customerOrder.canFulfill(currentPlayer.getHand())) {
                fulfillableCustomers.add(customerOrder);
            }
        }
        return fulfillableCustomers;
    }

    /**
     * Returns customers than can be garnished using the player's hand
     * @return customers than can be garnished using the player's hand
     */
    public Collection<CustomerOrder> getGarnishableCustomers() {
        List<CustomerOrder> customerOrders = customers.getActiveCustomers().stream().toList();
        Collection<CustomerOrder> garnishableCustomers = new ArrayList<>();
        for (int i=0; i<3; i++) {
            CustomerOrder customerOrder = customerOrders.get(i);
            if (customerOrder != null && customerOrder.canGarnish(currentPlayer.getHand()) && customerOrder.canFulfill(currentPlayer.getHand())) {
                garnishableCustomers.add(customerOrder);
            }
        }
        return garnishableCustomers;
    }

    /**
     * Gets the current unique Layers
     * @return Unique Layers collection
     */
    public Collection<Layer> getLayers() {
        return new ArrayList<>(new HashSet<>(layers));
    }

    /**
     * Gets the current Pantry
     * @return Pantry collection
     */
    public Collection<Ingredient> getPantry() {
        return pantry;
    }

    /**
     * Gets the current Players
     * @return Players collection
     */
    public Collection<Player> getPlayers() {
        return players;
    }

    /**
     * Loads saved bakery state from a file
     * @param file File to load bakery state from
     * @return Magic Bakery stored in file
     * @throws IOException If file read fails
     * @throws ClassNotFoundException If file doesn't contain Magic Bakery
     */
    public static MagicBakery loadState(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            return (MagicBakery) is.readObject();
        }
    }

    /**
     * Passes an ingredient card from one player to another
     * @param ingredient Card from the giving player
     * @param recipient Player to receive the card
     */
    public void passCard(Ingredient ingredient, Player recipient) {
        if (playerActionsRemaining == 0)
            throw new TooManyActionsException();
        if (!currentPlayer.hasIngredient(ingredient))
            throw new WrongIngredientsException("Player's hand doesn't contain ingredient");
        playerActionsRemaining -= 1;
        currentPlayer.removeFromHand(ingredient);
        recipient.addToHand(ingredient);
    }

    /**
     * Prints the current state of the customers
     */
    public void printCustomerServiceRecord() {
        System.out.printf(
            """
            Happy customers eating baked goods: %d (%d garnished)
            Gone to Greggs instead: %d
        """,
                customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.FULFILLED).size() + customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).size(),
                customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GARNISHED).size(),
                customers.getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP).size()
        );
    }

    /**
     * Prints the current game state
     */
    public void printGameState() {
        System.out.println("Layers:");
        for (String layerString: StringUtils.layersToStrings(getLayers()))
            System.out.println(layerString);
        System.out.println("Pantry:");
        for (String pantryString: StringUtils.ingredientsToStrings(getPantry()))
            System.out.println(pantryString);
        System.out.println("Waiting for service:");
        LinkedList<CustomerOrder> list = new LinkedList<>(customers.getActiveCustomers());
        Collections.reverse(list);
        for (String customerString: StringUtils.customerOrdersToStrings(list))
            System.out.println(customerString);
        printCustomerServiceRecord();
        System.out.printf("\n%s it's your turn. Your hand contains: %s\n", currentPlayer, currentPlayer.getHandStr());
    }

    /**
     * Move the pantry cards to the discard pile and draw five new cards from the deck
     */
    public void refreshPantry() {
        if (playerActionsRemaining == 0)
            throw new TooManyActionsException();
        playerActionsRemaining -= 1;
        pantryDiscard.addAll(pantry);
        pantry.clear();
        for (int i=0; i<Math.min(5, pantryDeck.size()); i++) {
            pantry.add(getCardFromDeck());
        }
    }


    /**
     * Saves bakery state to a file
     * @param file File to save bakery state to
     * @throws IOException If file write fails
     */
    public void saveState(File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(this);
        os.close();
    }

    /**
     * Starts the game and initialises variable
     * @param playerNames List of player names
     * @param customerDeckFile Path to customer file
     * @throws FileNotFoundException If customer file is not found
     */
    public void startGame(List<String> playerNames, String customerDeckFile) throws FileNotFoundException {
        // Initialise players
        int numOfPlayers= playerNames.size();

        if (numOfPlayers > 5 || numOfPlayers < 2)
            throw new IllegalArgumentException();

        for (String playerName: playerNames) {
            players.add(new Player(playerName));
        }
        currentPlayer = ((ArrayList<Player>) players).get(0);
        if (numOfPlayers == 2 || numOfPlayers == 3) {
            playerActionsRemaining = 3;
        } else {
            playerActionsRemaining = 2;
        }
        // Initialise customers
        customers = new Customers(customerDeckFile, random, layers, numOfPlayers);
        customers.addCustomerOrder();
        if (numOfPlayers == 3 || numOfPlayers == 5) {
            customers.addCustomerOrder();
        }
        // Shuffle pantry
        Collections.shuffle((ArrayList<Ingredient>)pantryDeck, random);

        // Initialise pantry
        for (int i=0; i<5; i++) {
            pantry.add(getCardFromDeck());
        }

        // Draw player cards
        for (int i=0; i<numOfPlayers; i++) {
            ((ArrayList<Player>) players).get(i).addToHand(getCardFromDeck());
            ((ArrayList<Player>) players).get(i).addToHand(getCardFromDeck());
            ((ArrayList<Player>) players).get(i).addToHand(getCardFromDeck());
        }
    }
}
