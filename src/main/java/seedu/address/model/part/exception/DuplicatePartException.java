package seedu.address.model.part.exception;

/**
 * Signals that the operation will result in duplicate Part (Parts are considered duplicates if they have the same
 * identity).
 */
public class DuplicatePartException extends RuntimeException {
    public DuplicatePartException() {
        super("Operation would result in duplicate parts");
    }
}
