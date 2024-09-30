package bakery;
/**
 * Represents a WrongIngredientsException
 * @author Ali Almuhtaseb

 * @version 1
 */
public class WrongIngredientsException extends IllegalArgumentException{
    /**
     * Throws an exception when the number of ingredients is not correct
     * @param msg Exception message
     */
    public WrongIngredientsException(String msg) {
        super(msg);
    }
}
