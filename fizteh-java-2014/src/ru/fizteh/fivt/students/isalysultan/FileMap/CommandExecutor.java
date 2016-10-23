package ru.fizteh.fivt.students.isalysultan.FileMap;

import java.io.IOException;

public class CommandExecutor {
    Commands newCommand;

    CommandExecutor() {
        newCommand = new Commands();
    }

    public void execute(Table tables, String[] command) throws IOException {
        switch (command[0]) {
        case "put":
            newCommand.put(command[1], command[2], tables);
            break;
        case "get":
            newCommand.get(command[1], tables);
            break;
        case "remove":
            newCommand.remove(command[1], tables);
            break;
        case "list":
            newCommand.list(tables);
            break;
        case "exit":
            tables.writeFile();
            System.out.println("exit");
            System.exit(0);
            break;
        default:
            System.out.println("Command is not recognized.");
        }
    }

}
