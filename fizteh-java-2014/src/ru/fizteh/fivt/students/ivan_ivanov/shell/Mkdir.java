package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.io.File;
import java.io.IOException;

public class Mkdir implements Command {

    public final String getName() {

        return "mkdir";
    }

    public final void executeCmd(final Shell shell, final String[] args) throws IOException {

        if (1 == args.length) {
            File theFile = new File(shell.getState().getPath().resolve(args[0]).toString());
            theFile.mkdir();
        } else {
            throw new IOException("Not correct name of directory");
        }
    }
}
