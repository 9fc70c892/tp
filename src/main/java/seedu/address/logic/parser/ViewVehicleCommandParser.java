package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewVehicleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.service.VehicleIdPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class ViewVehicleCommandParser implements Parser<ViewVehicleCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ViewVehicleCommand parse(String args) throws ParseException {

        try {
            Index index = ParserUtil.parseIndex(args);
            return new ViewVehicleCommand(new VehicleIdPredicate(index));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewVehicleCommand.MESSAGE_USAGE), pe);
        }
    }

}
