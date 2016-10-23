package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell;

import java.io.IOException;

public interface Command<FilesOperations> {

    String getCommandName();

    String getCommandParameters();

    void executeCommand(String parameters, FilesOperations operations)
            throws IOException;
}
