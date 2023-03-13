package seedu.address.model.part;

import seedu.address.model.entity.Entity;
import seedu.address.model.part.exception.InsufficientPart;

/**
 * This class represents a vehicle part.
 */
public class Part {

    private static int incrementalID = 0;
    private int id;
    private int stockLeft;
    private String name;
    private String description;
    private Entity purchasedFrom;
    private int cost;
    private PartType type;

    /**
     * This represents the various types of vehicle parts.
     */
    public enum PartType {
        WHEELS, SUSPENSION, FRAME, GEARBOX, BOLT, HEADLAMP, LIGHT, HORN, STEERING
    }

    /**
     * This method is the constructor class for a vehicle part.
     *
     * @param name The name of this vehicle part.
     * @param type The type of the vehicle part.
     * @param cost The cost of the vehicle part.
     * @param quantity How many in stock.
     */
    public Part(String name, PartType type, int cost, int quantity) {
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.stockLeft = quantity;
        id = ++incrementalID;
    }

    /**
     * This method is the constructor class for a vehicle part with a default stock of 0.
     *
     * @param name The name of this vehicle part.
     * @param type The type of the vehicle part.
     * @param cost The cost of the vehicle part.
     */
    public Part(String name, PartType type, int cost) {
        this(name, type, cost, 0);
    }

    /**
     * This method returns the ID of this part.
     * @return id of this part.
     */
    public int getId() {
        return id;
    }

    /**
     * This method returns how much stock (quantity) is left for the particular part.
     *
     * @return the quantity of this Part.
     */
    public int getStockLeft() {
        return stockLeft;
    }

    /**
     * This method sets how much stock (quantity) is left for this particular part.
     *
     * @param quantity The amount in stock.
     */
    public void setStockLeft(int quantity) {
        stockLeft = quantity;
    }

    /**
     * This method decrements the quantity of this part.
     *
     * @throws InsufficientPart
     */
    public void useStock() throws InsufficientPart {
        if (stockLeft < 1) {
            throw new InsufficientPart(this);
        }
        stockLeft--;
    }

    /**
     * This method returns the name of this part.
     *
     * @return The name of this part.
     */
    public String getName() {
        return name;
    }

    /**
     * This method sets the name of this part.
     *
     * @param name The name of this part.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns the description of this part.
     *
     * @return The description of this part.
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method sets the description of this part.
     *
     * @param description The description of this part.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method returns the Entity which sold us this part.
     *
     * @return The entity who sold us this part.
     */
    public Entity getPurchasedFrom() {
        return purchasedFrom;
    }

    /**
     * This method sets which Entity which sold us this part.
     *
     * @param purchasedFrom the Entity which sold us this part.
     */
    public void setPurchasedFrom(Entity purchasedFrom) {
        this.purchasedFrom = purchasedFrom;
    }

    /**
     * This method returns the cost of this Part.
     * @return the cost of this Part.
     */
    public int getCost() {
        return cost;
    }

    /**
     * This method sets the cost of this Part.
     *
     * @param cost the new cost of this Part.
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Returns true if both parts have the same name.
     * This defines a weaker notion of equality between two parts.
     */
    public boolean isSamePart(Part otherPart) {
        if (otherPart == this) {
            return true;
        }

        return otherPart != null
                && otherPart.getName().equals(getName());
    }

}
