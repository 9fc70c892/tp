package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalCustomers.getTypicalShop;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * @@author bakano98 reused
 * Solution adapted from
 * https://github.com/AY2122S2-CS2103T-T13-4/tp/commit/c896383a4b216daac2882ebc1b232684329dc59e
 */

public class UndoCommandTest {
    private static final StackUndoRedo EMPTY_STACK = new StackUndoRedo();

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), getTypicalShop());
    private final DeleteCustomerCommand deleteCommandOne = new DeleteCustomerCommand(1);
    private final DeleteCustomerCommand deleteCommandTwo = new DeleteCustomerCommand(2);

    @Test
    public void execute() throws Exception {
        deleteCommandOne.setData(EMPTY_STACK);
        deleteCommandTwo.setData(EMPTY_STACK);

        StackUndoRedo undoRedoStack = prepareStack(
                Arrays.asList(deleteCommandOne, deleteCommandTwo), Collections.emptyList());
        UndoCommand undoCommand = new UndoCommand();
        undoCommand.setData(undoRedoStack);
        deleteCommandOne.execute(model);
        deleteCommandTwo.execute(model);

        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs(), getTypicalShop());
        expectedModel.deleteCustomer(expectedModel.getFilteredCustomerList().get(INDEX_FIRST_PERSON.getZeroBased()));

        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_USAGE_SUCCESS, expectedModel);

        // single command in undoStack
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs(), getTypicalShop());
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_USAGE_SUCCESS, expectedModel);

        // no command in undoStack
        assertCommandFailure(undoCommand, model, UndoCommand.MESSAGE_USAGE_FAILURE);
    }

    public static StackUndoRedo prepareStack(List<RedoableCommand> undoElements,
                                             List<RedoableCommand> redoElements) {
        StackUndoRedo undoRedoStack = new StackUndoRedo();
        undoElements.forEach(undoRedoStack::push);

        Collections.reverse(redoElements);
        redoElements.forEach(undoRedoStack::push);
        redoElements.forEach(unused -> undoRedoStack.popUndo());

        return undoRedoStack;
    }
}