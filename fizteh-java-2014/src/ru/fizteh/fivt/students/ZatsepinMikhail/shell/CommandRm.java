package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandRm extends Command {
    public CommandRm() {
        name = "rm";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(final String[] arguments) {
        if (arguments.length != numberOfArguments
                & arguments.length != numberOfArguments - 1) {
            System.out.println("wrong number of arguments");
            return false;
        }
        boolean recursive =
                (arguments.length == numberOfArguments & arguments[1].equals("-r"));
        boolean result;
        if (recursive) {
            result = recursiveDelete(arguments);
        } else {
            result = generalDelete(arguments);
        }
        return result;
    }

    private boolean generalDelete(final String[] arguments) {
        if (numberOfArguments - 1 != arguments.length) {
            System.out.println("wrong number of arguments");
            return false;
        }
        Path filePath = PathsFunction.toAbsolutePathString(arguments[1]);
        try {
            if (!Files.deleteIfExists(filePath)) {
                System.out.println(name + ": cannot remove \'" + arguments[1] + "\'"
                                   + ": No such file or directory");
                return false;
            }
        } catch (Exception e) {
            if (Files.isDirectory(filePath)) {
                System.out.println(name + ": " + arguments[1] + ": is a directory");
            }
            return false;
        }
        return true;
    }

    private boolean recursiveDelete(final String[] arguments) {
        Path dirPath = PathsFunction.toAbsolutePathString(arguments[2]);
        FileVisitorDelete myFileVisitorDelete = new FileVisitorDelete();

        try {
            Files.walkFileTree(dirPath, myFileVisitorDelete);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
