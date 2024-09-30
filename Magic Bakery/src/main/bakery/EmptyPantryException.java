package bakery;

/**
 * Represents an EmptyPantry exception
 * @author Ali Almuhtaseb

 * @version 1
 */
public class EmptyPantryException extends RuntimeException{
    /**
     * Occurs only when the MagicBakery 's pantryDeck is completely empty, even after merging the pantryDeck and pantryDiscard
     * @param msg Exception message
     * @param e Root exception throwable
     */
    public EmptyPantryException(String msg, Throwable e) {
        super(msg, e);
    }
}
