package seedu.address.model.entity.shop;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.idgen.IdGenerator;
import seedu.address.model.ReadOnlyShop;
import seedu.address.model.entity.person.Address;
import seedu.address.model.entity.person.Customer;
import seedu.address.model.entity.person.Email;
import seedu.address.model.entity.person.Name;
import seedu.address.model.entity.person.Phone;
import seedu.address.model.entity.person.Technician;
import seedu.address.model.entity.shop.exception.AppointmentNotFoundException;
import seedu.address.model.entity.shop.exception.CustomerNotFoundException;
import seedu.address.model.entity.shop.exception.DuplicateEmailException;
import seedu.address.model.entity.shop.exception.DuplicatePhoneNumberException;
import seedu.address.model.entity.shop.exception.DuplicatePlateNumberException;
import seedu.address.model.entity.shop.exception.EmptyInputException;
import seedu.address.model.entity.shop.exception.InsufficientPartException;
import seedu.address.model.entity.shop.exception.InvalidDateException;
import seedu.address.model.entity.shop.exception.InvalidQuantityException;
import seedu.address.model.entity.shop.exception.NoFieldEditedException;
import seedu.address.model.entity.shop.exception.PartNotFoundException;
import seedu.address.model.entity.shop.exception.ServiceNotFoundException;
import seedu.address.model.entity.shop.exception.TechnicianNotFoundException;
import seedu.address.model.entity.shop.exception.VehicleNotFoundException;
import seedu.address.model.service.Service;
import seedu.address.model.service.ServiceStatus;
import seedu.address.model.service.Vehicle;
import seedu.address.model.service.VehicleType;
import seedu.address.model.service.appointment.Appointment;
import seedu.address.model.tag.Tag;

/**
 * A Shop is an entity that usually buy sells things.
 */
public class Shop implements ReadOnlyShop {
    public static final String MSG_RUNTIME_ERROR =
            "Relationships in shop broken, bug in one of the modification methods";
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private final ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
    private final ObservableList<Technician> technicians = FXCollections.observableArrayList();
    private final ObservableList<Service> services = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private final ObservableMap<String, Integer> parts =
            FXCollections.observableMap(new CaseInsensitiveHashMap<>());
    private final Logger logger = LogsCenter.getLogger(this.getClass());

    /**
     * Constructor for class Shop.
     */
    public Shop() {
    }

    /**
     * Creates a Shop using the data in the {@code toBeCopied}
     */
    public Shop(ReadOnlyShop toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    // Getters =========================================================================================================

    // Whole list

    /**
     * @return an unmodifiable view of the customers list.
     */
    public ObservableList<Customer> getCustomerList() {
        return FXCollections.unmodifiableObservableList(this.customers);
    }

    /**
     * @return an unmodifiable view of the vehicle list.
     */
    public ObservableList<Vehicle> getVehicleList() {
        return FXCollections.unmodifiableObservableList(this.vehicles);
    }

    /**
     * @return an unmodifiable view of the service list.
     */
    public ObservableList<Service> getServiceList() {
        return FXCollections.unmodifiableObservableList(this.services);
    }

    /**
     * @return an unmodifiable view of the technician list.
     */
    public ObservableList<Technician> getTechnicianList() {
        return FXCollections.unmodifiableObservableList(this.technicians);
    }

    /**
     * @return an unmodifiable view of the appointment list.
     */
    public ObservableList<Appointment> getAppointmentList() {
        return FXCollections.unmodifiableObservableList(this.appointments);
    }

    /**
     * @return an unmodifiable view of the parts map.
     */
    public ObservableList<Map.Entry<String, Integer>> getPartMap() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(this.parts.entrySet()));
    }

    // Individual item

    /**
     * @param id ID of the customer.
     * @return Customer with specified id.
     * @throws CustomerNotFoundException if the customer is not found.
     */
    private Customer getCustomer(int id) throws CustomerNotFoundException {
        return this.customers.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    /**
     * @param id ID of the vehicle.
     * @return Vehicle with specified id.
     * @throws VehicleNotFoundException if the vehicle is not found.
     */
    private Vehicle getVehicle(int id) throws VehicleNotFoundException {
        return this.vehicles.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    /**
     * @param id ID of the service.
     * @return Service with specified id.
     * @throws ServiceNotFoundException if the service is not found.
     */
    private Service getService(int id) throws ServiceNotFoundException {
        return this.services.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }

    /**
     * @param id ID of the technician.
     * @return Technician with specified id.
     * @throws TechnicianNotFoundException if the technician is not found.
     */
    private Technician getTechnician(int id) throws TechnicianNotFoundException {
        return this.technicians.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new TechnicianNotFoundException(id));
    }

    /**
     * @param id ID of the appointment.
     * @return Appointment with specified id.
     * @throws AppointmentNotFoundException if the appointment is not found.
     */
    private Appointment getAppointment(int id) throws AppointmentNotFoundException {
        return this.appointments.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    /**
     * @param partName Name of the part.
     * @return Quantity of the part.
     * @throws PartNotFoundException if the part is not found.
     */
    public int getPartQty(String partName) throws EmptyInputException, PartNotFoundException {
        if (partName.isBlank()) {
            throw new EmptyInputException("PartName cannot be blank");
        }
        if (!this.parts.containsKey(partName)) {
            throw new PartNotFoundException(partName);
        }
        return this.parts.get(partName);
    }

    // ================================================================================================

    // Checkers =======================================================================================

    /**
     * Checks if the customer exists in the shop.
     *
     * @param id id of the customer
     * @return true if the customer exists, false otherwise
     */
    public boolean hasCustomer(int id) {
        return this.customers.stream().anyMatch(c -> c.getId() == id);
    }

    /**
     * Checks if the vehicle exists in the shop.
     *
     * @param id id of the vehicle
     * @return true if the vehicle exists, false otherwise
     */
    public boolean hasVehicle(int id) {
        return this.vehicles.stream().anyMatch(v -> v.getId() == id);
    }

    /**
     * Checks if the service exists in the shop.
     *
     * @param id id of the service
     * @return true if the service exists, false otherwise
     */
    public boolean hasService(int id) {
        return this.services.stream().anyMatch(s -> s.getId() == id);
    }

    /**
     * Checks if the technician exists in the shop.
     *
     * @param id id of the technician
     * @return true if the technician exists, false otherwise
     */
    public boolean hasTechnician(int id) {
        return this.technicians.stream().anyMatch(t -> t.getId() == id);
    }

    /**
     * Checks if the appointment exists in the shop.
     *
     * @param id id of the appointment
     * @return true if the appointment exists, false otherwise
     */
    public boolean hasAppointment(int id) {
        return this.appointments.stream().anyMatch(a -> a.getId() == id);
    }

    /**
     * Checks if the part exists in the shop.
     *
     * @param partName name of the part
     * @return true if the part exists, false otherwise
     */
    public boolean hasPart(String partName) {
        return this.parts.containsKey(partName);
    }

    /**
     * Checks if the email exists in the shop.
     *
     * @param email email to check
     * @return true if email exists, false otherwise
     */
    private boolean emailExists(Email email) {
        return this.customers.stream().anyMatch(c -> c.getEmail().toString().equalsIgnoreCase(email.toString()))
                || this.technicians.stream().anyMatch(t -> t.getEmail().toString().equalsIgnoreCase(email.toString()));
    }

    /**
     * Checks if the phone exists in the shop.
     *
     * @param phone phone to check
     * @return true if phone exists, false otherwise
     */
    private boolean phoneExists(Phone phone) {
        return this.customers.stream().anyMatch(c -> c.getPhone().toString().equalsIgnoreCase(phone.toString()))
                || this.technicians.stream().anyMatch(t -> t.getPhone().toString().equalsIgnoreCase(phone.toString()));
    }

    /**
     * Checks if the plate number exists in the shop.
     *
     * @param plateNumber plate number to check
     * @return true if plate number exists, false otherwise
     */
    private boolean plateNumberExists(String plateNumber) {
        return this.vehicles.stream().anyMatch(v -> v.getPlateNumber().equalsIgnoreCase(plateNumber));
    }

    // ================================================================================================

    // Add ========================================================================================

    /**
     * Adds a customer to the shop.
     *
     * @param name    name of the customer
     * @param phone   phone number of the customer
     * @param email   email of the customer
     * @param address address of the customer
     * @param tags    tags of the customer
     * @throws DuplicateEmailException       if the email already exists
     * @throws DuplicatePhoneNumberException if the phone number already exists
     */
    public void addCustomer(Name name, Phone phone, Email email, Address address, Set<Tag> tags)
            throws DuplicateEmailException, DuplicatePhoneNumberException {
        if (this.emailExists(email)) {
            logger.info("Duplicate email " + email);
            throw new DuplicateEmailException(email);
        }
        if (this.phoneExists(phone)) {
            logger.info("Duplicate phone " + phone);
            throw new DuplicatePhoneNumberException(phone);
        }
        Customer toAdd = new Customer(IdGenerator.generateCustomerId(), name, phone, email, address, tags);
        this.customers.add(toAdd);
        logger.info(toAdd + " added");
    }

    /**
     * Adds a vehicle to the shop.
     *
     * @param ownerId     id of the owner of the vehicle
     * @param plateNumber plate number of the vehicle
     * @param color       color of the vehicle
     * @param brand       brand of the vehicle
     * @param type        type of the vehicle
     * @throws CustomerNotFoundException     if the customer is not found
     * @throws EmptyInputException           if the plate number, color or brand is blank
     * @throws DuplicatePlateNumberException if the plate number already exists
     */
    public void addVehicle(int ownerId, String plateNumber, String color, String brand, VehicleType type)
            throws CustomerNotFoundException, EmptyInputException, DuplicatePlateNumberException {
        if (plateNumber.isBlank()) {
            logger.info("Empty input for vehicle plate number");
            throw new EmptyInputException("Plate number should not be blank");
        }
        if (color.isBlank()) {
            logger.info("Empty input for vehicle color");
            throw new EmptyInputException("Color should not be blank");
        }
        if (brand.isBlank()) {
            logger.info("Empty input for vehicle brand");
            throw new EmptyInputException("Brand should not be blank");
        }
        if (this.plateNumberExists(plateNumber)) {
            logger.info("Duplicate plate number " + plateNumber);
            throw new DuplicatePlateNumberException(plateNumber);
        }
        Vehicle toAdd = new Vehicle(IdGenerator.generateVehicleId(), ownerId, plateNumber, color, brand, type);
        this.getCustomer(ownerId).addVehicle(toAdd.getId());
        this.vehicles.add(toAdd);
        logger.info(toAdd + " added");
    }

    /**
     * Adds a service to the shop.
     *
     * @param vehicleId                id of the vehicle
     * @param maybeEntryDate           entry date of the service
     * @param description              description of the service
     * @param maybeEstimatedFinishDate estimated finish date of the service
     * @param maybeServiceStatus       status of the service
     * @throws VehicleNotFoundException if the vehicle is not found
     * @throws EmptyInputException      if the description is blank
     * @throws InvalidDateException     if the date is invalid
     */
    public void addService(int vehicleId, Optional<LocalDate> maybeEntryDate, String description,
                           Optional<LocalDate> maybeEstimatedFinishDate, Optional<ServiceStatus> maybeServiceStatus)
            throws VehicleNotFoundException, EmptyInputException, InvalidDateException {
        if (description.isBlank()) {
            logger.info("Empty input for service description");
            throw new EmptyInputException("Description should not be blank");
        }
        LocalDate entryDate = maybeEntryDate.orElseGet(LocalDate::now);
        LocalDate estimatedFinishDate = maybeEstimatedFinishDate.orElseGet(() -> entryDate.plusDays(7));
        if (estimatedFinishDate.isBefore(entryDate)) {
            logger.info("Invalid date for service estimated finish date before entry date");
            throw new InvalidDateException(estimatedFinishDate, entryDate);
        }
        ServiceStatus serviceStatus = maybeServiceStatus.orElse(ServiceStatus.TO_REPAIR);
        Service toAdd = new Service(IdGenerator.generateServiceId(), vehicleId, entryDate, new HashMap<>(),
                description, estimatedFinishDate, serviceStatus);
        this.getVehicle(vehicleId).addService(toAdd);
        this.services.add(toAdd);
        logger.info(toAdd + " added");
    }

    /**
     * Adds a technician to the shop.
     *
     * @param name    name of the technician
     * @param phone   phone number of the technician
     * @param email   email of the technician
     * @param address address of the technician
     * @param tags    tags of the technician
     * @throws DuplicateEmailException       if the email already exists
     * @throws DuplicatePhoneNumberException if the phone number already exists
     */
    public void addTechnician(Name name, Phone phone, Email email, Address address, Set<Tag> tags)
            throws DuplicateEmailException, DuplicatePhoneNumberException {
        if (this.emailExists(email)) {
            logger.info("Duplicate email " + email);
            throw new DuplicateEmailException(email);
        }
        if (this.phoneExists(phone)) {
            logger.info("Duplicate phone " + phone);
            throw new DuplicatePhoneNumberException(phone);
        }
        Technician toAdd = new Technician(IdGenerator.generateStaffId(), name, phone, email, address, tags);
        this.technicians.add(toAdd);
        logger.info(toAdd + " added");
    }

    /**
     * Assigns existing technician to existing service.
     *
     * @param techId    id of the technician
     * @param serviceId id of the service
     * @throws TechnicianNotFoundException if the technician is not found
     * @throws ServiceNotFoundException    if the service is not found
     */
    public void addTechnicianToService(int techId, int serviceId)
            throws TechnicianNotFoundException, ServiceNotFoundException {
        Technician technician = this.getTechnician(techId);
        Service service = this.getService(serviceId);
        technician.addServiceId(serviceId);
        service.assignTechnician(techId);
    }

    /**
     * Assigns existing technician to existing appointment.
     *
     * @param techId        id of the technician
     * @param appointmentId id of the appointment
     * @throws TechnicianNotFoundException  if the technician is not found
     * @throws AppointmentNotFoundException if the appointment is not found
     */
    public void addTechnicianToAppointment(int techId, int appointmentId)
            throws TechnicianNotFoundException, AppointmentNotFoundException {
        Technician technician = this.getTechnician(techId);
        Appointment appointment = this.getAppointment(appointmentId);
        technician.addAppointmentId(appointmentId);
        appointment.addTechnician(techId);
    }

    /**
     * Adds appointment to the shop.
     *
     * @param customerId id of the customer
     * @param timeDate   time and date of the appointment
     * @throws CustomerNotFoundException if the customer is not found
     */
    public void addAppointment(int customerId, LocalDateTime timeDate) throws CustomerNotFoundException {
        Customer customer = this.getCustomer(customerId);
        Appointment toAdd = new Appointment(IdGenerator.generateAppointmentId(), customerId, timeDate);
        customer.addAppointment(toAdd);
        this.appointments.add(toAdd);
        logger.info(toAdd + " added");
    }

    /**
     * Adds a part to the shop.
     *
     * @param part name of the part
     * @param qty  quantity of the part
     * @throws EmptyInputException      if the part name is empty
     * @throws InvalidQuantityException if the quantity is invalid
     */
    public void addPart(String part, int qty)
            throws InvalidQuantityException, EmptyInputException {
        if (part.isBlank()) {
            throw new EmptyInputException("PartName cannot be blank");
        }
        if (qty <= 0) {
            throw new InvalidQuantityException(qty);
        }
        if (!this.hasPart(part)) {
            this.parts.put(part, qty);
        }
        this.parts.put(part, this.parts.get(part) + qty);
        logger.info(String.format("%s, %d added", part, qty));
    }

    /**
     * Adds a part to a service.
     *
     * @param serviceId id of the service
     * @param partName  name of the part
     * @param qty       quantity of the part
     * @throws PartNotFoundException     if the part is not found
     * @throws InsufficientPartException if the quantity of the part is insufficient
     * @throws ServiceNotFoundException  if the service is not found
     * @throws InvalidQuantityException  if the quantity is invalid
     * @throws EmptyInputException       If the part name is blank
     */
    public void addPartToService(int serviceId, String partName, int qty)
            throws PartNotFoundException, InsufficientPartException, ServiceNotFoundException,
                   InvalidQuantityException, EmptyInputException {
        if (partName.isBlank()) {
            throw new EmptyInputException("PartName cannot be blank");
        }
        if (qty <= 0) {
            throw new InvalidQuantityException(qty);
        }
        if (!this.hasPart(partName)) {
            throw new PartNotFoundException(partName);
        }
        if (this.getPartQty(partName) < qty) {
            throw new InsufficientPartException(partName, this.getPartQty(partName));
        }
        Service service = this.getService(serviceId);
        this.parts.put(partName, this.parts.get(partName) - qty);
        service.addPart(partName, qty);
    }

    // ==============================================================================================

    // Delete ==============================================================================================

    /**
     * Removes a customer from the shop. Customer's vehicles and appointments will be removed as well.
     *
     * @param customerId id of the customer
     * @throws CustomerNotFoundException if the customer is not found
     */
    public void removeCustomer(int customerId) throws CustomerNotFoundException {
        Customer toRemove = this.getCustomer(customerId);
        try {
            for (int i : toRemove.getVehicleIds()) {
                this.removeVehicle(i);
            }
            for (int i : toRemove.getAppointmentIds()) {
                this.removeAppointment(i);
            }
            this.customers.removeIf(c -> c.getId() == customerId);
            logger.info(String.format("Customer %d removed", customerId));
            IdGenerator.setCustomerIdUnused(customerId);
        } catch (VehicleNotFoundException | AppointmentNotFoundException e) {
            logger.severe(e.getMessage());
            throw new RuntimeException(MSG_RUNTIME_ERROR);
        }
    }

    /**
     * Removes a vehicle from the shop. Vehicle's services will be removed as well. Vehicle's owner will be updated.
     *
     * @param vehicleId id of the vehicle
     * @throws VehicleNotFoundException if the vehicle is not found
     */
    public void removeVehicle(int vehicleId) throws VehicleNotFoundException {
        Vehicle toRemove = this.getVehicle(vehicleId);
        try {
            for (int i : toRemove.getServiceIds()) {
                this.removeService(i);
            }
            this.getCustomer(toRemove.getOwnerId()).removeVehicle(toRemove);
            this.vehicles.removeIf(v -> v.getId() == vehicleId);
            logger.info(String.format("Vehicle %d removed", vehicleId));
            IdGenerator.setVehicleIdUnused(vehicleId);
        } catch (ServiceNotFoundException | CustomerNotFoundException e) {
            logger.severe(e.getMessage());
            throw new RuntimeException(MSG_RUNTIME_ERROR);
        }
    }

    /**
     * Removes appointment from the shop. Appointment's customer and technicians will be updated.
     *
     * @param appointmentId id of the appointment
     * @throws AppointmentNotFoundException if the appointment is not found
     */
    public void removeAppointment(int appointmentId) throws AppointmentNotFoundException {
        Appointment toRemove = this.getAppointment(appointmentId);
        try {
            this.getCustomer(toRemove.getCustomerId()).removeAppointment(toRemove);
            for (int i : toRemove.getStaffIds()) {
                this.getTechnician(i).removeAppointmentIds(x -> x == i);
            }
            this.appointments.removeIf(a -> a.getId() == appointmentId);
            logger.info(String.format("Appointment %d removed", appointmentId));
            IdGenerator.setAppointmentIdUnused(appointmentId);
        } catch (CustomerNotFoundException | TechnicianNotFoundException e) {
            logger.severe(e.getMessage());
            throw new RuntimeException(MSG_RUNTIME_ERROR);
        }
    }

    /**
     * Removes a service from the shop. Service's vehicle and technicians will be updated.
     *
     * @param serviceId id of the service
     * @throws ServiceNotFoundException if the service is not found
     */
    public void removeService(int serviceId) throws ServiceNotFoundException {
        Service toRemove = this.getService(serviceId);
        try {
            for (var entry : toRemove.getRequiredParts().entrySet()) {
                String partName = entry.getKey();
                int qty = entry.getValue();
                if (this.parts.containsKey(partName)) {
                    this.parts.put(partName, this.parts.get(partName) + qty);
                } else {
                    this.parts.put(partName, qty);
                }
            }
            this.getVehicle(toRemove.getVehicleId()).removeService(toRemove);
            for (int i : toRemove.getAssignedToIds()) {
                this.getTechnician(i).removeServiceIds(x -> x == serviceId);
            }
            this.services.removeIf(s -> s.getId() == serviceId);
            logger.info(String.format("Service %d removed", serviceId));
            IdGenerator.setServiceIdUnused(serviceId);
        } catch (VehicleNotFoundException | TechnicianNotFoundException ex) {
            logger.severe(ex.getMessage());
            throw new RuntimeException(MSG_RUNTIME_ERROR);
        }
    }

    /**
     * Removes a technician from the shop. Technician's services and appointments will be updated.
     *
     * @param techId id of the technician
     * @throws TechnicianNotFoundException if the technician is not found
     */
    public void removeTechnician(int techId) throws TechnicianNotFoundException {
        Technician toRemove = this.getTechnician(techId);
        try {
            for (int i : toRemove.getServiceIds()) {
                this.getService(i).removeTechnician(toRemove);
            }
            for (int i : toRemove.getAppointmentIds()) {
                this.getAppointment(i).removeTechnician(toRemove.getId());
            }
            this.technicians.removeIf(t -> t.getId() == techId);
            logger.info("Technician %d removed");
            IdGenerator.setStaffIdUnused(techId);
        } catch (ServiceNotFoundException | AppointmentNotFoundException e) {
            logger.severe(e.getMessage());
            throw new RuntimeException(MSG_RUNTIME_ERROR);
        }
    }

    /**
     * Removes part quantity from the shop.
     *
     * @param name     name of the part
     * @param quantity quantity of the part to be removed
     * @throws PartNotFoundException     if the part is not found
     * @throws InsufficientPartException if the quantity to be removed is more than the quantity in the shop
     * @throws InvalidQuantityException  if the quantity to be removed is less than or equal to 0
     * @throws EmptyInputException       if the part name is blank
     */
    public void removePart(String name, int quantity)
            throws PartNotFoundException, InsufficientPartException, InvalidQuantityException, EmptyInputException {
        if (name.isBlank()) {
            throw new EmptyInputException("PartName cannot be blank");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }
        if (this.getPartQty(name) < quantity) {
            throw new InsufficientPartException(name, quantity);
        }
        int newQty = this.getPartQty(name) - quantity;
        this.parts.put(name, newQty);
        logger.info(String.format("%s x %d removed", name, quantity));
    }

    /**
     * Deletes a part from the shop.
     *
     * @param name name of the part
     * @throws PartNotFoundException if the part is not found
     * @throws EmptyInputException   if the part name is blank
     */
    public void deletePart(String name) throws PartNotFoundException, EmptyInputException {
        if (name.isBlank()) {
            throw new EmptyInputException("PartName cannot be blank");
        }
        if (!this.parts.containsKey(name)) {
            throw new PartNotFoundException(name);
        }
        this.parts.remove(name);
        logger.info(String.format("%s deleted", name));
    }

    /**
     * Removes a part from a service. Part will be transferred back to the shop.
     *
     * @param partName  name of the part
     * @param serviceId id of the service
     * @throws PartNotFoundException    if the part is not found
     * @throws ServiceNotFoundException if the service is not found
     * @throws EmptyInputException      if the part name is blank
     */
    public void removePartFromService(String partName, int serviceId, int qtyToRemove)
            throws PartNotFoundException,
                   ServiceNotFoundException,
                   EmptyInputException,
                   InsufficientPartException,
                   InvalidQuantityException {
        if (partName.isBlank()) {
            throw new EmptyInputException("PartName cannot be blank");
        }
        if (qtyToRemove <= 0) {
            throw new InvalidQuantityException(qtyToRemove);
        }
        Service service = this.getService(serviceId);
        if (!service.getRequiredParts().containsKey(partName)) {
            throw new PartNotFoundException(partName);
        }
        int qty = service.getRequiredParts().get(partName);
        if (qty < qtyToRemove) {
            throw new InsufficientPartException(partName, qtyToRemove);
        }
        if (qty == qtyToRemove) {
            service.getRequiredParts().remove(partName);
        }
        if (this.parts.containsKey(partName)) {
            this.parts.put(partName, this.getPartQty(partName) + qty);
        } else {
            this.parts.put(partName, qty);
        }
        service.getRequiredParts().remove(partName);
        logger.info(String.format("%s x % d transferred back to shop", partName, qty));
    }

    /**
     * Removes a technician from a service. Technician will be unassigned from the service.
     *
     * @param techId    id of the technician
     * @param serviceId id of the service
     * @throws TechnicianNotFoundException if the technician is not found
     * @throws ServiceNotFoundException    if the service is not found
     */
    public void removeTechnicianFromService(int techId, int serviceId)
            throws TechnicianNotFoundException, ServiceNotFoundException {
        Service service = this.getService(serviceId);
        Technician technician = this.getTechnician(techId);
        service.removeTechnician(technician);
        logger.info(String.format("Technician %d unassigned from service %d", techId, serviceId));
    }

    /**
     * Removes a technician from an appointment. Technician will be unassigned from the appointment.
     *
     * @param techId        id of the technician
     * @param appointmentId id of the appointment
     * @throws TechnicianNotFoundException  if the technician is not found
     * @throws AppointmentNotFoundException if the appointment is not found
     */
    public void removeTechnicianFromAppointment(int techId, int appointmentId)
            throws TechnicianNotFoundException, AppointmentNotFoundException {
        Appointment appointment = this.getAppointment(appointmentId);
        Technician technician = this.getTechnician(techId);
        appointment.removeTechnician(techId);
        logger.info(String.format("Technician %d unassigned from appointment %d", techId, appointmentId));
    }

    // ================================================================================================================

    // Edit ===========================================================================================================

    /**
     * Edits a customer's details.
     *
     * @param customerId id of the customer
     * @param name       new name of the customer
     * @param phone      new phone number of the customer
     * @param email      new email of the customer
     * @param address    new address of the customer
     * @param tags       new tags of the customer
     * @throws CustomerNotFoundException     if the customer is not found
     * @throws DuplicateEmailException       if the email is already used by another customer
     * @throws DuplicatePhoneNumberException if the phone number is already used by another customer
     * @throws NoFieldEditedException        if no field is edited
     */
    public void editCustomer(int customerId,
                             Optional<Name> name,
                             Optional<Phone> phone,
                             Optional<Email> email,
                             Optional<Address> address,
                             Optional<Set<Tag>> tags)
            throws CustomerNotFoundException,
                   DuplicateEmailException, DuplicatePhoneNumberException, NoFieldEditedException {
        if (allEmpty(name, phone, email, address, tags)) {
            throw new NoFieldEditedException();
        }
        Customer customer = this.getCustomer(customerId);
        Name newName = name.orElse(customer.getName());
        Phone newPhone = phone.orElse(customer.getPhone());
        Email newEmail = email.orElse(customer.getEmail());
        Address newAddress = address.orElse(customer.getAddress());
        Set<Tag> newTags = tags.orElse(customer.getTags());
        if (this.emailExists(newEmail) && !newEmail.equals(customer.getEmail())) {
            throw new DuplicateEmailException(newEmail);
        }
        if (this.phoneExists(newPhone) && !newPhone.equals(customer.getPhone())) {
            throw new DuplicatePhoneNumberException(newPhone);
        }
        Customer editedCustomer = new Customer(customerId, newName, newPhone, newEmail, newAddress, newTags,
                new HashSet<>(customer.getVehicleIds()), new HashSet<>(customer.getAppointmentIds()));
        int index = this.customers.indexOf(customer);
        this.customers.set(index, editedCustomer);
        logger.info(String.format("Customer %d edited", customerId));
    }

    /**
     * Edits a vehicle's details.
     * If owner id changes, the vehicle will be removed from the old owner and added to the new owner.
     *
     * @param vehicleId   id of the vehicle
     * @param ownerId     new owner id of the vehicle
     * @param plateNumber new plate number of the vehicle
     * @param color       new color of the vehicle
     * @param brand       new brand of the vehicle
     * @param type        new type of the vehicle
     * @throws VehicleNotFoundException      if the vehicle is not found
     * @throws CustomerNotFoundException     if the customer is not found
     * @throws EmptyInputException           if the plate number, color or brand is blank
     * @throws DuplicatePlateNumberException if the plate number is already used by another vehicle
     * @throws NoFieldEditedException        if no field is edited
     */
    public void editVehicle(int vehicleId,
                            Optional<Integer> ownerId,
                            Optional<String> plateNumber,
                            Optional<String> color,
                            Optional<String> brand,
                            Optional<VehicleType> type)
            throws VehicleNotFoundException,
                   CustomerNotFoundException,
                   EmptyInputException,
                   DuplicatePlateNumberException,
                   NoFieldEditedException {
        if (allEmpty(ownerId, plateNumber, color, brand, type)) {
            throw new NoFieldEditedException();
        }
        Vehicle vehicle = this.getVehicle(vehicleId);
        int newOwnerId = ownerId.orElse(vehicle.getOwnerId());
        String newPlateNumber = plateNumber.orElse(vehicle.getPlateNumber());
        String newColor = color.orElse(vehicle.getColor());
        String newBrand = brand.orElse(vehicle.getBrand());
        VehicleType newType = type.orElse(vehicle.getType());
        if (this.plateNumberExists(newPlateNumber) && !newPlateNumber.equals(vehicle.getPlateNumber())) {
            throw new DuplicatePlateNumberException(newPlateNumber);
        }
        if (newPlateNumber.isBlank()) {
            throw new EmptyInputException("PlateNumber cannot be blank");
        }
        if (newColor.isBlank()) {
            throw new EmptyInputException("Color cannot be blank");
        }
        if (newBrand.isBlank()) {
            throw new EmptyInputException("Brand cannot be blank");
        }
        Vehicle editedVehicle = new Vehicle(vehicleId,
                newOwnerId, newPlateNumber, newColor, newBrand, newType, vehicle.getServiceIdsSet());
        if (newOwnerId != vehicle.getOwnerId()) {
            Customer prevOwner = this.getCustomer(vehicle.getOwnerId());
            Customer newOwner = this.getCustomer(newOwnerId);
            prevOwner.removeVehicle(vehicle);
            newOwner.addVehicle(vehicle);
        }
        int index = this.vehicles.indexOf(vehicle);
        this.vehicles.set(index, editedVehicle);
        logger.info(String.format("Vehicle %d edited", vehicleId));
    }

    /**
     * Edits a service's details.
     * If vehicle id changes, the service will be removed from the old vehicle and added to the new vehicle.
     *
     * @param serviceId           ID of the service
     * @param vehicleId           new ID of attached vehicle
     * @param entryDate           new entry date of the service
     * @param description         new description of the service
     * @param estimatedFinishDate new estimated finish date of the service
     * @param serviceStatus       new status of the service
     * @throws ServiceNotFoundException if the service is not found
     * @throws VehicleNotFoundException if the vehicle is not found
     * @throws EmptyInputException      if the description is blank
     * @throws InvalidDateException     if the estimated finish date is before the entry date
     */
    public void editService(int serviceId,
                            Optional<Integer> vehicleId,
                            Optional<LocalDate> entryDate,
                            Optional<String> description,
                            Optional<LocalDate> estimatedFinishDate,
                            Optional<ServiceStatus> serviceStatus
    )
            throws ServiceNotFoundException,
                   VehicleNotFoundException,
                   EmptyInputException,
                   InvalidDateException,
                   NoFieldEditedException {
        if (allEmpty(vehicleId, entryDate, description, estimatedFinishDate, serviceStatus)) {
            throw new NoFieldEditedException();
        }
        Service service = this.getService(serviceId);
        int newVehicleId = vehicleId.orElse(service.getVehicleId());
        LocalDate newEntryDate = entryDate.orElse(service.getEntryDate());
        String newDescription = description.orElse(service.getDescription());
        LocalDate newEstimatedFinishDate = estimatedFinishDate.orElse(service.getEstimatedFinishDate());
        ServiceStatus newServiceStatus = serviceStatus.orElse(service.getStatus());
        if (newDescription.isBlank()) {
            throw new EmptyInputException("Description cannot be blank");
        }
        if (newEstimatedFinishDate.isBefore(newEntryDate)) {
            throw new InvalidDateException(newEstimatedFinishDate, newEntryDate);
        }

        Service editedService = new Service(serviceId,
                newVehicleId, newEntryDate, service.getRequiredParts(),
                newDescription, newEstimatedFinishDate, newServiceStatus, service.getAssignedToIdsSet());
        if (newVehicleId != service.getVehicleId()) {
            Vehicle prevVehicle = this.getVehicle(service.getVehicleId());
            Vehicle newVehicle = this.getVehicle(newVehicleId);
            prevVehicle.removeService(service);
            newVehicle.addService(editedService);
        }
        int index = this.services.indexOf(service);
        this.services.set(index, editedService);
        logger.info(String.format("Service %d edited", serviceId));
    }

    /**
     * Edits an appointment's details.
     * If customer id changes, the appointment will be removed from the old customer and added to the new customer.
     *
     * @param appointmentId ID of the appointment
     * @param customerId    new ID of attached customer
     * @param timeDate      new time and date of the appointment
     * @throws AppointmentNotFoundException if the appointment is not found
     * @throws CustomerNotFoundException    if the customer is not found
     * @throws NoFieldEditedException       if no field is edited
     */
    public void editAppointment(int appointmentId,
                                Optional<Integer> customerId,
                                Optional<LocalDateTime> timeDate)
            throws AppointmentNotFoundException, CustomerNotFoundException, NoFieldEditedException {
        if (allEmpty(customerId, timeDate)) {
            throw new NoFieldEditedException();
        }
        Appointment appointment = this.getAppointment(appointmentId);
        int newCustomerId = customerId.orElse(appointment.getCustomerId());
        LocalDateTime newTimeDate = timeDate.orElse(appointment.getTimeDate());

        Appointment editedAppointment = new Appointment(appointmentId,
                newCustomerId, newTimeDate, new HashSet<>(appointment.getStaffIds()));

        if (newCustomerId != appointment.getCustomerId()) {
            Customer prevCustomer = this.getCustomer(appointment.getCustomerId());
            Customer newCustomer = this.getCustomer(newCustomerId);
            prevCustomer.removeAppointment(appointment);
            newCustomer.addAppointment(editedAppointment);
        }
        int index = this.appointments.indexOf(appointment);
        this.appointments.set(index, editedAppointment);
        logger.info(String.format("Appointment %d edited", appointmentId));
    }

    /**
     * Edits a technician's details.
     *
     * @param technicianId ID of the technician
     * @param name         new name of the technician
     * @param phone        new phone number of the technician
     * @param email        new email of the technician
     * @param address      new address of the technician
     * @param tags         new tags of the technician
     * @throws TechnicianNotFoundException   if the technician is not found
     * @throws DuplicateEmailException       if the email is already used by another technician
     * @throws DuplicatePhoneNumberException if the phone number is already used by another technician
     * @throws NoFieldEditedException        if no field is edited
     */
    public void editTechnician(int technicianId,
                               Optional<Name> name,
                               Optional<Phone> phone,
                               Optional<Email> email,
                               Optional<Address> address,
                               Optional<Set<Tag>> tags)
            throws TechnicianNotFoundException,
                   DuplicateEmailException,
                   DuplicatePhoneNumberException,
                   NoFieldEditedException {
        if (allEmpty(name, phone, email, address, tags)) {
            throw new NoFieldEditedException();
        }
        Technician technician = this.getTechnician(technicianId);
        Name newName = name.orElse(technician.getName());
        Phone newPhone = phone.orElse(technician.getPhone());
        Email newEmail = email.orElse(technician.getEmail());
        Address newAddress = address.orElse(technician.getAddress());
        Set<Tag> newTags = tags.orElse(technician.getTags());
        if (this.emailExists(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }
        if (this.phoneExists(newPhone)) {
            throw new DuplicatePhoneNumberException(newPhone);
        }
        Technician editedTechnician = new Technician(technicianId, newName, newPhone, newEmail, newAddress, newTags,
                new HashSet<>(technician.getServiceIds()), new HashSet<>(technician.getAppointmentIds()));
        int index = this.technicians.indexOf(technician);
        this.technicians.set(index, editedTechnician);
        logger.info(String.format("Technician %d edited", technicianId));
    }

    // =================================================================================================================

    // Additional helper methods  ======================================================================================

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyShop newData) {
        requireNonNull(newData);

        this.customers.clear();
        this.customers.addAll(newData.getCustomerList());

        this.vehicles.clear();
        this.vehicles.addAll(newData.getVehicleList());

        this.parts.clear();
        newData.getPartMap().forEach(entry -> this.parts.put(entry.getKey(), entry.getValue()));

        this.services.clear();
        this.services.addAll(newData.getServiceList());

        this.technicians.clear();
        this.technicians.addAll(newData.getTechnicianList());

        this.appointments.clear();
        this.appointments.addAll(newData.getAppointmentList());

        logger.info("Shop data reset");
    }

    /**
     * Replaces the existing data of this {@code shop} with {@code newData}.
     * This should only be called when loading data from file
     */
    public void overrideData(ObservableList<Customer> customers,
                             ObservableList<Vehicle> vehicles,
                             ObservableMap<String, Integer> parts,
                             ObservableList<Service> services,
                             ObservableList<Technician> technicians,
                             ObservableList<Appointment> appointments) {
        this.customers.clear();
        this.customers.addAll(customers);

        this.vehicles.clear();
        this.vehicles.addAll(vehicles);

        this.parts.clear();
        this.parts.putAll(parts);

        this.services.clear();
        this.services.addAll(services);

        this.technicians.clear();
        this.technicians.addAll(technicians);

        this.appointments.clear();
        this.appointments.addAll(appointments);

        logger.info("Shop data overridden");
    }

    /**
     * checks if all the optional arguments are empty
     */
    private static boolean allEmpty(Optional<?>... optionals) {
        return Arrays.stream(optionals).allMatch(Optional::isEmpty);
    }

    // --------------------------------------------------
    //// util methods

    //    @Override
    //    public String toString() {
    //        return persons.asUnmodifiableObservableList().size() + " persons";
    //        // TODO: refine later
    //        // TODO: modify this
    //    }
    //
}
