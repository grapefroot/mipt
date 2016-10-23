package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;

import java.io.IOException;

public class Exit implements Command {

    public final String getName() {
        return "exit";
    }

    public final void executeCmd(final Shell filemap, final String[] args) throws IOException {
        Utils.write(((FileMap) filemap).getFileMapState().getDataBase(),
                ((FileMap) filemap).getFileMapState().getDataFile());
        System.exit(0);
    }
}
