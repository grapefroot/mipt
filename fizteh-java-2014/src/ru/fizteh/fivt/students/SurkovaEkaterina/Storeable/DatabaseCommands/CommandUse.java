package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.CommandsParser;

public class CommandUse<Table, Key, Value, TableOperations extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandUse() {
        super("use", "use <table name>");
    }

    public final void executeCommand(final String params,
                                     final TableOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        Table newTable = null;
        if (parameters.length > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("Not enough arguments!");
        }
        try {
            newTable = ops.useTable(parameters[0]);
        } catch (IllegalStateException e) {
            System.err.println(String.format("wrong type (%s)", e.getMessage()));
            return;
        }

        if (newTable == null) {
            System.out.println(String.format("%s not exists", parameters[0]));
            return;
        }

        System.out.println(String.format("using %s", ops.getActiveTableName()));
    }
}

