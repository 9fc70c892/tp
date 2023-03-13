package seedu.address.model.part;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.part.exception.DuplicatePartException;
import seedu.address.model.part.exception.PartNotFoundException;

/**
 * A list of persons that enforces uniqueness between its elements and does not allow nulls.
 * A person is considered unique by comparing using {@code Person#isSamePerson(Person)}. As such, adding and updating of
 * persons uses Person#isSamePerson(Person) for equality so as to ensure that the person being added or updated is
 * unique in terms of identity in the UniquePersonList. However, the removal of a person uses Person#equals(Object) so
 * as to ensure that the person with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Part#isSamePart(Part)
 */
public class UniquePartList implements Iterable<Part> {

    private final ObservableList<Part> internalList = FXCollections.observableArrayList();
    private final ObservableList<Part> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent person as the given argument.
     */
    public boolean contains(Part toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSamePart);
    }

    /**
     * Adds a person to the list.
     * The person must not already exist in the list.
     */
    public void add(Part toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicatePartException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the list.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the list.
     */
    public void setPerson(Part target, Part editedPart) {
        requireAllNonNull(target, editedPart);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new PartNotFoundException();
        }

        if (!target.isSamePart(editedPart) && contains(editedPart)) {
            throw new DuplicatePartException();
        }

        internalList.set(index, editedPart);
    }

    /**
     * Removes the equivalent person from the list.
     * The person must exist in the list.
     */
    public void remove(Part toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new PartNotFoundException();
        }
    }

    public void setParts(UniquePartList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setParts(List<Part> parts) {
        requireAllNonNull(parts);
        if (!partsAreUnique(parts)) {
            throw new DuplicatePartException();
        }

        internalList.setAll(parts);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Part> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Part> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniquePartList // instanceof handles nulls
                        && internalList.equals(((UniquePartList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code persons} contains only unique persons.
     */
    private boolean partsAreUnique(List<Part> parts) {
        for (int i = 0; i < parts.size() - 1; i++) {
            for (int j = i + 1; j < parts.size(); j++) {
                if (parts.get(i).isSamePart(parts.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
