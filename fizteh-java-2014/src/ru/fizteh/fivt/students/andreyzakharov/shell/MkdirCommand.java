package ru.fizteh.fivt.students.andreyzakharov.shell;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MkdirCommand extends AbstractCommand {
    MkdirCommand(Shell shell) {
        super("mkdir", shell);
    }

    @Override
    public void execute(String... args) {
        if (args.length < 2) {
            shell.error("mkdir: missing operand");
            return;
        }
        if (args.length > 2) {
            shell.error("mkdir: too many arguments");
            return;
        }
        Path path = shell.getWd().resolve(args[1]);
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            shell.error("mkdir: cannot create directory '" + args[1] + "': File exists");
        } catch (IOException e) {
            shell.error("mkdir: cannot create directory '" + args[1] + "': No such file or directory");
        }
    }
}
