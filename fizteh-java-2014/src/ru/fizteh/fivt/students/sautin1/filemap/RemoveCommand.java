package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.students.sautin1.filemap.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.filemap.shell.UserInterruptException;

/**
 * Remove command.
 * Created by sautin1 on 10/12/14.
 */
public class RemoveCommand extends AbstractStringDatabaseCommand {

    public RemoveCommand() {
        super("remove", 1, 1);
    }

    /**
     * Remove entry by its key from the active table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        String value = state.getActiveTable().remove(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
