package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap.FileMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.FileMap.FileMapShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.Shell.CommandsParser;

public class CommandSize<Table, Key, Value, FileMapShellOperations
        extends FileMapShellOperationsInterface<Table, Key, Value>>
        extends ACommand<FileMapShellOperations>  {
    public CommandSize() {
        super("size", "size");
    }

    public void executeCommand(String params, FileMapShellOperations shellState) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("size: Too many parameters!");
        }

        int size = shellState.size();
        System.out.println(size);
    }
}
