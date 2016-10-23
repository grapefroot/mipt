package ru.fizteh.fivt.students.sautin1.junit.multifilemap;

/**
 * Created by sautin1 on 10/20/14.
 */

import ru.fizteh.fivt.students.sautin1.junit.filemap.AbstractStringDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringDatabaseState;
import ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException;

/**
 * Command for deleting database table.
 * Created by sautin1 on 10/20/14.
 */
public class DropCommand extends AbstractStringDatabaseCommand {

    public DropCommand() {
        super("drop", 1, 1);
    }

    /**
     * Create new table.
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
        try {
            if (args[1].equals(state.getActiveTable().getName())) {
                state.setActiveTable(null);
            }
            state.getTableProvider().removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalArgumentException e) {
            System.err.println(args[1] + " not exists");
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
