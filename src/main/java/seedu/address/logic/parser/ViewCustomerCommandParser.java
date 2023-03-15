package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewCustomerCommand;
import seedu.address.logic.commands.ViewVehicleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.entity.person.CustomerIdPredicate;
import seedu.address.model.service.VehicleIdPredicate;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class ViewCustomerCommandParser implements Parser<ViewCustomerCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ViewCustomerCommand parse(String args) throws ParseException {

        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewCustomerCommand(new CustomerIdPredicate(index));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCustomerCommand.MESSAGE_USAGE), pe);
        }
    }

}