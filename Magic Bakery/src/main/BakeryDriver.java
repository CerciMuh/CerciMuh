import java.io.IOException;
import java.util.List;

import bakery.MagicBakery;
import util.ConsoleUtils;

public class BakeryDriver {

    public BakeryDriver() {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        MagicBakery bakery;

        ConsoleUtils consoleUtils = new ConsoleUtils();
        if (!consoleUtils.promptForStartLoad("Would you like to start a new game, or load an existing one?")) {
            bakery = MagicBakery.loadState(consoleUtils.promptForFilePath("Enter path to saved game state:"));
        } else {
            bakery = new MagicBakery(123, "io/ingredients.csv", "io/layers.csv");
            List<String> players = consoleUtils.promptForNewPlayers("Let's get started! Who's playing?");
            bakery.startGame(players, "io/customers.csv");
        }

        boolean gameRunning = true;
        while (gameRunning) {
            while (bakery.getActionsRemaining() != 0) {
                bakery.printGameState();
                String prompt = String.format("You have %d actions remaining. What do you want to do?", bakery.getActionsRemaining());
                bakery.doAction(consoleUtils.promptForAction(prompt, bakery));
            }
            gameRunning = !bakery.endTurn();
        }
        bakery.printCustomerServiceRecord();
    }

}