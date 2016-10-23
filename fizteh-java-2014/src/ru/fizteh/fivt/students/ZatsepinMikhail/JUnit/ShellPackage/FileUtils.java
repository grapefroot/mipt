package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.ShellPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.shell.CommandMkdir;
import ru.fizteh.fivt.students.ZatsepinMikhail.shell.CommandRm;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static boolean mkdir(Path newDirectory) {
        CommandMkdir command = new CommandMkdir();
        String[] args = {"mkdir",
                newDirectory.toString()
        };
        return command.run(args);
    }

    public static boolean rmdir(Path newDirectory) {
        CommandRm command = new CommandRm();
        if (!Files.isDirectory(newDirectory) | !Files.exists(newDirectory)) {
            return false;
        }
        String[] args = {"rm",
                "-r",
                newDirectory.toString()
        };
        return command.run(args);
    }
}
