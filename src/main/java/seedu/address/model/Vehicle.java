package seedu.address.model;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.service.Service;

/**
 * The vehicle class is the superclass of all types of vehicle.
 * As a start, you may wish to consider VehicleType of creating other subclasses.
 */
public class Vehicle {
    private static int incrementalID = 0;
    private int id;
    private String plateNumber;
    private String color;
    private String brand;
    private VehicleType type;
    private List<Service> services;

    /**
     * This enum allows us to specify vehicle type without creating respective classes.
     */
    public enum VehicleType {
        MOTORBIKE, CAR
    }

    /**
     * This method is the constructor for the Vehicle class.
     * @param plateNumber The Vehicle Registration Number (VRN) of the vehicle.
     * @param color This is the color of the vehicle.
     * @param brand This is the brand of the vehicle (i.e. KIA).
     * @param type What vehicle type this vehicle is (i.e. Bike)
     */
    public Vehicle(String plateNumber, String color, String brand, VehicleType type) {
        id = ++incrementalID;
        this.plateNumber = plateNumber;
        this.color = color;
        this.brand = brand;
        this.type = type;
        this.services = new ArrayList<>();
    }

    /**
     * This method returns the id of this vehicle.
     * This is not the VRN number.
     *
     * @return The id of this vehicle.
     */
    public int getId() {
        return id;
    }

    /**
     * This method returns the VRN number of this vehicle.
     *
     * @return the plate number of this vehicle.
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * This method sets the VRN number of this vehicle.
     * Primarily used of the vehicle's VRN has been changed.
     *
     * @param plateNumber the new plate number of this vehicle.
     */
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    /**
     * This method returns the current color of this vehicle.
     *
     * @return the color of this vehicle.
     */
    public String getColor() {
        return color;
    }

    /**
     * This method changes the color of this vehicle.
     *
     * @param color the new color of this vehicle.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * This method returns the brand of this vehicle.
     *
     * @return the brand of this vehicle.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * This method sets the brand of this vehicle.
     * Only to be used if errors has been made.
     *
     * @param brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * This returns the VehicleType of this vehicle.
     * i.e. returns that it is a car or a motorbike.
     *
     * @return the type of this vehicle.
     */
    public VehicleType getType() {
        return type;
    }

    /**
     * This method changes the VehicleType of this vehicle.
     *
     * @param type  the VehicleType to be changed to.
     */
    public void setType(VehicleType type) {
        this.type = type;
    }

    /**
     * This method returns the services that is needed to be done to this vehicle.
     *
     * @return List of services
     */
    public List<Service> getServices() {
        return services;
    }

    /**
     * This method adds the service that is needed to be done to this vehicle.
     *
     * @param service the service to be added.
     */
    public void addService(Service service) {
        this.services.add(service);
    }

    /**
     * This method removes a service that was added to this vehicle...
     *
     * @param service the service to be removed.
     */
    public void removeService(Service service) {
        this.services.remove(service);
    }

}
