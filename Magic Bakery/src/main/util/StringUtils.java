package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.Layer;
/**
 * Utility class for the console
 * @author Ali Almuhtaseb

 * @version 1
 */
public final class StringUtils {

    private static final String INDENT = "  ";
    private static final String CARD_HORIZONTAL = "-";
    private static final String CARD_VERTICAL = "|";
    private static final String CARD_VERTICAL_LEFT = CARD_VERTICAL + " ";
    private static final String CARD_VERTICAL_RIGHT = " " + CARD_VERTICAL;

    private StringUtils() {
        // Used to ensure that there is no public/default constructor.
        // This class provides static utility functions, it's not meant to be
        // instantiated.
    }

    private static String centreString(int width, final String s) {
        if (s.endsWith("⌛")) {
            width--;
        }
        if (s.equals(Ingredient.HELPFUL_DUCK.toString())) {
            ++width;
        }
        return String.format(
            "%-" + width  + "s", String.format(
                "%" + (s.length() + (width - s.length()) / 2) + "s", s
            )
        );
    }

    /**
     * Gets a multi-line String representation of the specified CustomerOrders.
     *
     * @param customerOrders The CustomerOrders to represent.
     * @return The multi-line String representation.
     */
    public static List<String> customerOrdersToStrings(
        final Collection<CustomerOrder> customerOrders
    ) {
        final int CUSTOMER_CARD_WIDTH_INNER = 36;

        List<String> rtn = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        List<String> recipeHeads = new ArrayList<String>();
        ArrayList<ArrayList<String>> recipes = new ArrayList<ArrayList<String>>();
        List<String> garnishHeads = new ArrayList<String>();
        List<String> garnishes = new ArrayList<String>();

        // Figure out how long the longest recipe is (i.e. number of lines)
        int maxNumberOfRecipeLines = 0;
        for (CustomerOrder customer : customerOrders) {
            if (customer == null) {
                continue;
            }
            maxNumberOfRecipeLines = Math.max(
                maxNumberOfRecipeLines, StringUtils.splitString(
                    CUSTOMER_CARD_WIDTH_INNER,
                    customer.getRecipeDescription(), ","
                ).size()
            );
        }
        for (int lineNo = 0; lineNo < maxNumberOfRecipeLines; ++lineNo) {
            recipes.add(new ArrayList<String>());
        }

        // Build each block of card content (name, recipe header, recipe,
        // garnish header, garnish)
        Iterator<CustomerOrder> iterator = (
            (LinkedList<CustomerOrder>) customerOrders
        ).descendingIterator();
        while (iterator.hasNext()) {
            CustomerOrder customer = iterator.next();
            String orderName = "";
            String recipeHead = "";
            String recipe = "";
            String garnishHead = "";
            String garnish = "";
            if (customer != null) {
                orderName = customer.toString().toUpperCase();
                if (customer.getStatus().equals(
                    CustomerOrder.CustomerOrderStatus.IMPATIENT
                )) {
                    orderName += "⌛";
                }
                recipe = StringUtils.toTitleCase(
                    customer.getRecipeDescription()
                );
                if (customer.getRecipeDescription().length() > 0) {
                    recipeHead = "Recipe: ";
                }
                if (customer.getGarnishDescription().length() > 0) {
                    garnishHead = "Garnish: ";
                    garnish = StringUtils.toTitleCase(
                        customer.getGarnishDescription()
                    );
                }
            }
            names.add(
                StringUtils.centreString(CUSTOMER_CARD_WIDTH_INNER, orderName)
            );
            recipeHeads.add(
                StringUtils.padString(CUSTOMER_CARD_WIDTH_INNER, recipeHead)
            );
            garnishHeads.add(padString(CUSTOMER_CARD_WIDTH_INNER, garnishHead));
            List<String> recipeLines = StringUtils.splitString(
                CUSTOMER_CARD_WIDTH_INNER, recipe, ","
            );
            while (recipeLines.size() < maxNumberOfRecipeLines) {
                recipeLines.add("");
            }
            for (int lineNo = 0; lineNo < maxNumberOfRecipeLines; ++lineNo) {
                String recipeLine = centreString(
                    CUSTOMER_CARD_WIDTH_INNER, recipeLines.get(lineNo)
                );
                recipes.get(lineNo).add(recipeLine);
            }
            garnishes.add(
                StringUtils.centreString(CUSTOMER_CARD_WIDTH_INNER, garnish)
            );
        }

        // Turn each block into one or more Strings representing lines of
        // card content
        rtn.add(StringUtils.formatTextAsCardsString(
            customerOrders.size(), StringUtils.CARD_HORIZONTAL.repeat(
                CUSTOMER_CARD_WIDTH_INNER
            )
        ));
        rtn.add(StringUtils.formatTextAsCardsString(names));
        rtn.add(StringUtils.formatTextAsCardsString(recipeHeads));
        for (ArrayList<String> recipe : recipes) {
            rtn.add(StringUtils.formatTextAsCardsString(recipe));
        }
        rtn.add(StringUtils.formatTextAsCardsString(garnishHeads));
        rtn.add(StringUtils.formatTextAsCardsString(garnishes));
        rtn.add(StringUtils.formatTextAsCardsString(
            customerOrders.size(), StringUtils.CARD_HORIZONTAL.repeat(
                CUSTOMER_CARD_WIDTH_INNER
            )
        ));

        return rtn;
    }

    private static String formatTextAsCardsString(
        final List<String> cardTexts
    ) {
        String out = StringUtils.INDENT;
        for (String cardText : cardTexts) {
            out += StringUtils.CARD_VERTICAL_LEFT + cardText;
            out += StringUtils.CARD_VERTICAL_RIGHT;
        }
        return out;
    }

    private static String formatTextAsCardsString(
        final int n, final String cardText
    ) {
        return StringUtils.INDENT + (
            StringUtils.CARD_VERTICAL_LEFT + cardText +
            StringUtils.CARD_VERTICAL_RIGHT
        ).repeat(n);
    }

    /**
     * Gets a multi-line String representation of the specified Ingredients.
     *
     * @param ingredients The Ingredients to represent.
     * @return The multi-line String representation.
     */
    public static List<String> ingredientsToStrings(
        final Collection<Ingredient> ingredients
    ) {
        final int INGREDIENT_CARD_WIDTH_INNER = 20;

        List<String> rtn = new ArrayList<String>();
        List<String> names = new ArrayList<String>();

        // Build each block of card content (name)
        for (Ingredient ing : ingredients) {
            names.add(StringUtils.centreString(
                INGREDIENT_CARD_WIDTH_INNER, ing.toString()
            ).toUpperCase());
        }

        // Turn each block into one or more Strings representing lines of
        // card content
        rtn.add(StringUtils.formatTextAsCardsString(
            ingredients.size(), StringUtils.CARD_HORIZONTAL.repeat(
                INGREDIENT_CARD_WIDTH_INNER
            )
        ));
        rtn.add(StringUtils.formatTextAsCardsString(names));
        rtn.add(StringUtils.formatTextAsCardsString(
            ingredients.size(), StringUtils.CARD_HORIZONTAL.repeat(
                INGREDIENT_CARD_WIDTH_INNER
            )
        ));

        return rtn;
    }

    /**
     * Gets a multi-line String representation of the specified Layers.
     *
     * @param layers The Layers to represent.
     * @return The multi-line String representation.
     */
    public static List<String> layersToStrings(final Collection<Layer> layers) {
        final int LAYER_CARD_WIDTH_INNER = 16;

        List<String> rtn = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        ArrayList<ArrayList<String>> recipes = new ArrayList<ArrayList<String>>();

        ArrayList<Layer> layersOrd = new ArrayList<Layer>(layers);
        layersOrd.sort((o1, o2) -> o1.toString().compareTo(o2.toString()));

        // Figure out how long the longest recipe is (i.e. number of lines)
        int maxNumberOfRecipeLines = 0;
        for (Layer layer : layersOrd) {
            maxNumberOfRecipeLines = Math.max(
                maxNumberOfRecipeLines, StringUtils.splitString(
                    LAYER_CARD_WIDTH_INNER, layer.getRecipeDescription(), ","
                ).size()
            );
        }
        for (int lineNo = 0; lineNo < maxNumberOfRecipeLines; ++lineNo) {
            recipes.add(new ArrayList<String>());
        }

        // Build each block of card content (name, recipe header, recipe)
        for (Layer layer : layersOrd) {
            names.add(StringUtils.centreString(
                    LAYER_CARD_WIDTH_INNER, layer.toString().toUpperCase()
            ));

            List<String> recipeLines = StringUtils.splitString(
                LAYER_CARD_WIDTH_INNER, layer.getRecipeDescription(), ","
            );
            while (recipeLines.size() < maxNumberOfRecipeLines) {
                recipeLines.add("");
            }
            for (int lineNo = 0; lineNo < maxNumberOfRecipeLines; ++lineNo) {
                String recipeLine = centreString(
                    LAYER_CARD_WIDTH_INNER, StringUtils.toTitleCase(
                        recipeLines.get(lineNo)
                    )
                );
                recipes.get(lineNo).add(recipeLine);
            }
        }

        // Turn each block into one or more Strings representing lines of
        // card content
        rtn.add(StringUtils.formatTextAsCardsString(
            layersOrd.size(), StringUtils.CARD_HORIZONTAL.repeat(
                LAYER_CARD_WIDTH_INNER
            )
        ));
        rtn.add(StringUtils.formatTextAsCardsString(names));
        rtn.add(StringUtils.formatTextAsCardsString(
            layersOrd.size(), StringUtils.padString(
                LAYER_CARD_WIDTH_INNER, "Recipe: "
            )
        ));
        for (ArrayList<String> recipe : recipes) {
            rtn.add(StringUtils.formatTextAsCardsString(recipe));
        }
        rtn.add(StringUtils.formatTextAsCardsString(
            layersOrd.size(), StringUtils.CARD_HORIZONTAL.repeat(
                LAYER_CARD_WIDTH_INNER
            )
        ));

        return rtn;
    }

    private static String padString(final int width, final String s) {
        return s + " ".repeat(width - s.length()); // left align
    }

    /**
     * Splits a String into mutiple substring chunks where the longest chunk
     * is not more than width characters in length.
     *
     * @param width The max number of characters per chunk
     * @param sourceString The String to split.
     * @param separator A separator to split on (e.g. if width is 20 but
     * there's an instance of separator at character 18, then it will split
     * at character 18 instead of 20).
     * @return A list of substrings.
     */
    public static List<String> splitString(
        final int width, final String sourceString, final String separator
    ) {
        List<String> rtn = new ArrayList<String>();
        int start = 0;
        int end = Math.min(sourceString.length(), width);

        boolean trim = !separator.isEmpty();
        trim = trim && end != sourceString.length();
        String substr = sourceString.substring(start, end);
        trim = trim && substr.lastIndexOf(separator) > -1;
        end = trim ? substr.lastIndexOf(separator) + 1 : end;

        while (start < sourceString.length()) {
            substr = sourceString.substring(start, end);
            rtn.add(substr.strip());
            start = end;
            end = Math.min(end + width, sourceString.length());
            trim = end != sourceString.length();
            trim = trim && substr.lastIndexOf(separator) > -1;
            end = trim ? substr.lastIndexOf(separator) + start + 1 : end;
        }
        return rtn;
    }

    /**
     * Replaces the last occurance of the specifed target substring within
     * a String. If the target isn't present within the specified string,
     * then the original string is returned.
     *
     * @param str The string to replace into.
     * @param target The substring to be replaced.
     * @param replacement The replacement to be made.
     * @return The modified string.
     */
    public static String replaceLast(
        final String str, final String target, final String replacement
    ) {
        int pos = str.lastIndexOf(target);
        if (pos > -1) {
            return str.substring(0, pos) + replacement + str.substring(
                pos + target.length()
            );
        }
        return str;
    }

    /**
     * Converts the given comma-separated string to one in which each comma-
     * separated element begins with an uppercase letter and the rest is
     * lower case. E.g. "BLUE, moon" becomes "Blue, Moon".
     *
     * @param str the string to convert.
     * @return the converted string.
     */
    public static String toTitleCase(final String str) {
        final String SEP = ", ";
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Arrays.stream(str.split(SEP)).map(
            word -> word.isEmpty() ? word : Character.toTitleCase(
                word.charAt(0)
            ) + word.substring(1).toLowerCase()
        ).collect(Collectors.joining(SEP));
    }

}
