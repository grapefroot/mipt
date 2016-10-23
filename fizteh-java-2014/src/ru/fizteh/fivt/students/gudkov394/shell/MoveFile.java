package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/*работает*/

public class MoveFile {
    public MoveFile(final String[] currentArgs, final CurrentDirectory cd) {
        if (currentArgs.length != 3) {
            System.err.println("wrong number arguments for mv");
            System.exit(1);
        }
        CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
        File from = new File(currentArgs[1]);
        File to = new File(currentArgs[2]);
        if (!from.isAbsolute()) {
            from = new File(cd.getCurrentDirectory(), currentArgs[1]);
        }
        if (!to.isAbsolute()) {
            to = new File(cd.getCurrentDirectory(), currentArgs[2]);
        }
        if (from.toString().equals(to.toString())) {
            System.err.println("source and destination are equal");
            System.exit(2);
        }
        try {
            Files.move(from.toPath(), to.toPath(), options);
        } catch (IOException e) {
            System.err.println("problem with move");
            System.exit(4);
        }
    }
}
