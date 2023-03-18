package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CUSTOMER_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_APPOINTMENTS;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.service.appointment.Appointment;


/**
 * Edits the details of an existing person in the address book.
 */
public class EditAppointmentCommand extends RedoableCommand {

    public static final String COMMAND_WORD = "editappointment";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the appointment identified "
            + "by the id number displayed by listappointments. "
            + "Existing values will be overwritten by the input values.\n"
            + "Note that if " + PREFIX_DATE + " is used, then " + PREFIX_TIME + " must accompany it, and vice versa."
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_CUSTOMER_ID + "CUSTOMER ID]"
            + "[" + PREFIX_DATE + "DATE  "
            + PREFIX_TIME + "TIME]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_CUSTOMER_ID + "5 "
            + PREFIX_DATE + "2023-02-03 "
            + PREFIX_TIME + "17:00";

    public static final String MESSAGE_EDIT_APPOINTMENT_SUCCESS = "Edited Appointment: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_APPOINTMENT = "This appointment already exists in AutoM8";
    public static final String MESSAGE_APPOINTMENT_NOT_FOUND = "Appointment not in AutoM8";

    private static final Appointment APPOINTMENT_DOES_NOT_EXIST = null;

    private final EditAppointmentDescriptor editAppointmentDescriptor;

    private int id;

    /**
     * @param editAppointmentDescriptor details to edit the person with
     */
    public EditAppointmentCommand(EditAppointmentDescriptor editAppointmentDescriptor) {
        requireNonNull(editAppointmentDescriptor);
        this.editAppointmentDescriptor = new EditAppointmentDescriptor(editAppointmentDescriptor);
    }

    @Override
    public CommandResult executeUndoableCommand(Model model) throws CommandException {
        requireNonNull(model);
        List<Appointment> lastShownList = model.getFilteredAppointmentList();

        // Locate Appointment containing id. By right each ID is unique.
        Appointment appointmentToEdit = lastShownList.stream().filter(appointment ->
                        editAppointmentDescriptor.getId() == appointment.getId()).findAny()
                .orElse(APPOINTMENT_DOES_NOT_EXIST);

        // If appointment doesn't exist
        if (appointmentToEdit == null) {
            throw new CommandException(MESSAGE_APPOINTMENT_NOT_FOUND);
        }

        Appointment editedAppointment = createEditedAppointment(appointmentToEdit, editAppointmentDescriptor);

        if (!appointmentToEdit.isSameAppointment(editedAppointment) && model.hasAppointment(editedAppointment)) {
            throw new CommandException(MESSAGE_DUPLICATE_APPOINTMENT);
        }

        model.setAppointment(appointmentToEdit, editedAppointment);
        model.updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENTS);
        return new CommandResult(String.format(MESSAGE_EDIT_APPOINTMENT_SUCCESS, editedAppointment));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Appointment createEditedAppointment(Appointment appointmentToEdit,
                                                       EditAppointmentDescriptor editAppointmentDescriptor) {
        assert appointmentToEdit != null;

        int id = appointmentToEdit.getId(); // Can't have them custom inserting id.
        int customerId = editAppointmentDescriptor.getCustomerId().orElse(appointmentToEdit.getCustomerId());
        LocalDateTime timeDate = editAppointmentDescriptor.getTimeDate().orElse(appointmentToEdit.getTimeDate());
        Set<Integer> staffIds = editAppointmentDescriptor.getStaffIds().orElse(
                (Set<Integer>) appointmentToEdit.getStaffIds());

        return new Appointment(id, customerId, timeDate, staffIds);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditAppointmentCommand)) {
            return false;
        }

        // state check
        EditAppointmentCommand e = (EditAppointmentCommand) other;
        return editAppointmentDescriptor.equals(e.editAppointmentDescriptor);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditAppointmentDescriptor {
        // This is basically a copy of Appointment
        private int id;
        private int customerId;
        private LocalDateTime timeDate;
        private Set<Integer> staffIds = new HashSet<>();

        public EditAppointmentDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditAppointmentDescriptor(EditAppointmentDescriptor toCopy) {
            setId(toCopy.id);
            setCustomerId(toCopy.customerId);
            setTimeDate(toCopy.timeDate);
            setStaffIds(toCopy.staffIds);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {

            return CollectionUtil.isAnyNonNull(customerId, timeDate, staffIds);
        }

        // Users should not be allowed to run this, but is needed for creating the class
        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setCustomerId(int id) {
            this.customerId = id;
        }

        public Optional<Integer> getCustomerId() {
            return Optional.ofNullable(customerId);
        }

        public void setTimeDate(LocalDateTime timeDate) {
            this.timeDate = timeDate;
        }

        public Optional<LocalDateTime> getTimeDate() {
            return Optional.ofNullable(timeDate);
        }

        public void setStaffIds(Set<Integer> staffIds) {

            this.staffIds = (staffIds != null) ? new HashSet<>(staffIds) : null;;
        }

        public Optional<Set<Integer>> getStaffIds() {
            return (staffIds != null) ? Optional.of(Collections.unmodifiableSet(staffIds)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditAppointmentDescriptor)) {
                return false;
            }

            // state check
            EditAppointmentDescriptor e = (EditAppointmentDescriptor) other;

            return getId() == (e.getId())
                    && getCustomerId().equals(e.getCustomerId())
                    && getTimeDate().equals(e.getTimeDate())
                    && getStaffIds().equals(e.getStaffIds());
        }
    }
}
