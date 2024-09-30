package bakery;

/**
 * Represents a TooManyActionsException
 * @author Ali Almuhtaseb

 * @version 1
 */
public class TooManyActionsException extends IllegalStateException {
    /**
     * Throws an exception when a player tries to take more actions than the maximum amount
     */
    public TooManyActionsException() {
        super();
    }
    /**
     * Throws an exception when a player tries to take more actions than the maximum amount
     * @param msg Exception message
     */
    public TooManyActionsException(String msg) {
        super(msg);
    }
}
