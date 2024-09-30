package bakery;

import java.io.FileNotFoundException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import util.CardUtils;

/**
 * Represents Customers
 * @author Ali Almuhtaseb


 * @version 1
 */
public class Customers implements Serializable {
    private final Collection<CustomerOrder> activeCustomers;
    private Collection<CustomerOrder> customerDeck;
    private final List<CustomerOrder> inactiveCustomers;
    private final Random random;

    @Serial
    final private static long serialVersionUID = 1L;

    /**
     * Constructor for Customers
     * @param deckFile Path to customer order cards file
     * @param random Same random object initialised in MagicBakery
     * @param layers List of layers
     * @param numPlayers Number of players
     * @throws FileNotFoundException If deck file is not found
     */
    public Customers(String deckFile, Random random, Collection<Layer> layers, int numPlayers) throws FileNotFoundException {
        this.random = random;
        inactiveCustomers = new ArrayList<>();
        initialiseCustomerDeck(deckFile, layers, numPlayers);
        activeCustomers = new ArrayList<>();
        activeCustomers.add(null);
        activeCustomers.add(null);
        activeCustomers.add(null);
    }

    /**
     * Adds a new customer order to activeCustomers, drawn from customerDeck
     * @return Customer that left the shop
     */
    public CustomerOrder addCustomerOrder() {
        CustomerOrder customerOrder = timePasses();
        CustomerOrder newOrder = drawCustomer();
        if (newOrder != null) {
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, newOrder);
            setImpatient();
        } else {
            throw new EmptyStackException();
        }
        return customerOrder;
    }

    /**
     * Indicates whether a customer will leave at the end of the round
     * @return If a customer will leave at the end of the round
     */
    public boolean customerWillLeaveSoon() {
        if (((ArrayList<CustomerOrder>) activeCustomers).get(0) != null && ((ArrayList<CustomerOrder>) activeCustomers).get(1) != null && ((ArrayList<CustomerOrder>) activeCustomers).get(2) != null) return true;
        if (customerDeck.isEmpty() && ((ArrayList<CustomerOrder>) activeCustomers).get(0) == null && ((ArrayList<CustomerOrder>) activeCustomers).get(2) != null) return true;
        return false;
    }

    /**
     * Returns the topmost card in the customer deck
     * @return the topmost card in the customer deck
     */
    public CustomerOrder drawCustomer() {
        if (getCustomerDeck().isEmpty()) return null;
        return ((ArrayList<CustomerOrder>) customerDeck).remove(customerDeck.size()-1);
    }

    /**
     * Returns the active customers
     * @return The active customers
     */
    public Collection<CustomerOrder> getActiveCustomers() {
        return activeCustomers;
    }

    /**
     * Returns the customer deck
     * @return The customer deck
     */
    public Collection<CustomerOrder> getCustomerDeck() {
        return customerDeck;
    }

    /**
     * Returns the CustomerOrders in activeCustomer that can be fulfilled using only the Ingredients contained in the parameter hand
     * @param hand List of ingredients
     * @return CustomersOrders than can be fulfilled
     */
    public Collection<CustomerOrder> getFulfillable(List<Ingredient> hand) {
        Collection<CustomerOrder> customers = new ArrayList<>();
        List<CustomerOrder> activeCustomerList = new ArrayList<>(activeCustomers);
        if (activeCustomerList.get(0) != null && activeCustomerList.get(0).canFulfill(hand)) {
            customers.add(activeCustomerList.get(0));
        }
        if (activeCustomerList.get(1) != null && activeCustomerList.get(1).canFulfill(hand)) {
            customers.add(activeCustomerList.get(1));
        }
        if (activeCustomerList.get(2) != null && activeCustomerList.get(2).canFulfill(hand)) {
            customers.add(activeCustomerList.get(2));
        }
        return customers;
    }

    /**
     * Returns the inactiveCustomers whose status matches the specified CustomerOrderStatus
     * @param status Status to filter inactiveCustomers by
     * @return List of CustomerOrders with the given status
     */
    public Collection<CustomerOrder> getInactiveCustomersWithStatus(CustomerOrder.CustomerOrderStatus status) {
        return inactiveCustomers.stream().filter(customerOrder -> customerOrder.getStatus() == status).collect(Collectors.toCollection(ArrayList::new));
    }

    private void initialiseCustomerDeck(String deckFile, Collection<Layer> layers, int numPlayers) throws FileNotFoundException {
        customerDeck = new ArrayList<>();
        List<CustomerOrder> customerOrders = CardUtils.readCustomerFile(deckFile, layers);
        Collections.shuffle(customerOrders, random);
        List<CustomerOrder> levelOne = customerOrders.stream().filter(customerOrder -> customerOrder.getLevel() == 1).collect(Collectors.toCollection(ArrayList::new));
        List<CustomerOrder> levelTwo = customerOrders.stream().filter(customerOrder -> customerOrder.getLevel() == 2).collect(Collectors.toCollection(ArrayList::new));
        List<CustomerOrder> levelThree = customerOrders.stream().filter(customerOrder -> customerOrder.getLevel() == 3).collect(Collectors.toCollection(ArrayList::new));
        if (numPlayers == 2) {
            customerDeck.addAll(levelOne.subList(0, 4));
            customerDeck.addAll(levelTwo.subList(0, 2));
            customerDeck.addAll(levelThree.subList(0, 1));
        } else if (numPlayers == 3 || numPlayers == 4) {
            customerDeck.addAll(levelOne.subList(0, 1));
            customerDeck.addAll(levelTwo.subList(0, 2));
            customerDeck.addAll(levelThree.subList(0, 4));
        } else if (numPlayers == 5) {
            customerDeck.addAll(levelTwo.subList(0, 1));
            customerDeck.addAll(levelThree.subList(0, 6));
        }
        Collections.shuffle((ArrayList<CustomerOrder>)customerDeck, random);
    }

    /**
     * Returns true if there are currently no customers in activeCustomers
     * @return {@code true} if activeCustomers is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return !(((ArrayList<CustomerOrder>) activeCustomers).get(0) != null ||((ArrayList<CustomerOrder>) activeCustomers).get(1) != null || ((ArrayList<CustomerOrder>) activeCustomers).get(2) != null);
    }

    /**
     * Returns the rightmost customer order in activeCustomers, or null if it is empty
     * @return Rightmost customer order in activeCustomers
     */
    public CustomerOrder peek() {
        return ((ArrayList<CustomerOrder>) activeCustomers).get(2);
    }

    /**
     * Removes the specified customer from activeCustomers
     * @param customer Customer to remove from activeCustomers
     */
    public void remove(CustomerOrder customer) {
        int index = ((ArrayList<CustomerOrder>) activeCustomers).indexOf(customer);
        ((ArrayList<CustomerOrder>) activeCustomers).set(index, null);
        inactiveCustomers.add(customer);
    }

    /**
     * Returns the number of customers in activeCustomers
     * @return Number of customers in activeCustomers
     */
    public int size() {
        int size = 0;
        if (((ArrayList<CustomerOrder>) activeCustomers).get(0) != null) size += 1;
        if (((ArrayList<CustomerOrder>) activeCustomers).get(1) != null) size += 1;
        if (((ArrayList<CustomerOrder>) activeCustomers).get(2) != null) size += 1;
        return size;
    }

    /**
     * Updates the status of the last customer in activeCustomers
     */
    public void setImpatient() {
        List<CustomerOrder> customerOrders = new ArrayList<>(activeCustomers);
        CustomerOrder customer = customerOrders.get(2);
        if (customerWillLeaveSoon()) {
            customer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            customerOrders.set(2, customer);
        } else if (customer != null) {
            customer.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
            customerOrders.set(2, customer);
        }
    }

    /**
     * Passes time in a round and moves activeCustomers forward
     * @return Customer that left the shop
     */
    public CustomerOrder timePasses() {
        boolean deckCustomer = !getCustomerDeck().isEmpty();

        CustomerOrder firstCustomer = ((ArrayList<CustomerOrder>) activeCustomers).get(0);
        CustomerOrder secondCustomer = ((ArrayList<CustomerOrder>) activeCustomers).get(1);
        CustomerOrder thirdCustomer = ((ArrayList<CustomerOrder>) activeCustomers).get(2);

        // First row
        if (deckCustomer && firstCustomer != null && secondCustomer != null && thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP);
            inactiveCustomers.add(thirdCustomer);
//            secondCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, secondCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return thirdCustomer;
        }
        // Second row
        if (deckCustomer && firstCustomer != null && secondCustomer != null) {
//            secondCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, secondCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return null;
        }
        // Third row
        if (deckCustomer && firstCustomer != null && thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, thirdCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return null;
        }
        // Fourth row
        if (deckCustomer && firstCustomer == null && secondCustomer != null && thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, thirdCustomer);
            return null;
        }
        // Fifth row
        if (deckCustomer && firstCustomer != null) {
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return null;
        }
        // Sixth row
        if (deckCustomer && secondCustomer != null) {
            return null;
        }
        // Seventh row
        if (deckCustomer && thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.WAITING);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, thirdCustomer);
            return null;
        }
        // Eighth row
        if (deckCustomer) {
            return null;
        }
        // Ninth row
        if (firstCustomer != null && secondCustomer != null && thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP);
            inactiveCustomers.add(thirdCustomer);
            secondCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, secondCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return thirdCustomer;
        }
        // Tenth row
        if (firstCustomer != null && secondCustomer != null) {
            secondCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, secondCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return null;
        }
        // Eleventh row
        if (firstCustomer != null && thirdCustomer != null) {
//            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, thirdCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return null;
        }
        // Twelfth row
        if (firstCustomer == null && secondCustomer != null && thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP);
            inactiveCustomers.add(thirdCustomer);
            secondCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, secondCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, null);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return thirdCustomer;
        }
        // Thirteenth row
        if (firstCustomer != null) {
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, firstCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(0, null);
            return null;
        }
        // Fourteenth row
        if (secondCustomer != null) {
            secondCustomer.setStatus(CustomerOrder.CustomerOrderStatus.IMPATIENT);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, secondCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(1, null);
            return null;
        }
        // Fifteenth row
        if (thirdCustomer != null) {
            thirdCustomer.setStatus(CustomerOrder.CustomerOrderStatus.GIVEN_UP);
            inactiveCustomers.add(thirdCustomer);
            ((ArrayList<CustomerOrder>) activeCustomers).set(2, null);
            return thirdCustomer;
        }
        // Sixteenth row
        return null;
    }
}
