package ru.fizteh.fivt.students.SibgatullinDamir.FileMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Lenovo on 30.09.2014.
 */
public class FileMapMain {

    public static void main(String[] args) {
        try {
            currentPath = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));
        } catch (NullPointerException e) {
            System.err.println("Can't open file");
            System.exit(1);
        }
        fileMap = new FileMap(currentPath);

        if (args.length == 0) {
            FileMapMain.interfaceMode();
        } else {
            FileMapMain.packageMode(args);
        }
    }

    static Path currentPath;
    static FileMap fileMap;

    private static void switchCommand(String[] command) throws MyException {

        Map<String, Commands> commands = new HashMap<String, Commands>();
        commands.put(new PutCommand().getName(), new PutCommand());
        commands.put(new GetCommand().getName(), new GetCommand());
        commands.put(new ListCommand().getName(), new ListCommand());
        commands.put(new RemoveCommand().getName(), new RemoveCommand());
        commands.put(new ExitCommand().getName(), new ExitCommand());

        try {
            commands.get(command[0]).execute(command, fileMap);
        } catch (NullPointerException e) {
            throw new MyException("No such command");
        }
    }

    private static void interfaceMode() {
        Scanner input = new Scanner(System.in);
        System.out.print("$ ");
        while (true) {
            if (!input.hasNextLine()) {
                System.exit(0);
            }
            String com = input.nextLine();
            if (com.length() == 0) {
                System.out.print("$ ");
                continue;
            }
            String[] commands = com.split(";");
            for (String string: commands) {
                try {
                    String[] command = string.trim().split("\\s+");
                    if (command.length == 1 && command[0].length() == 0) {
                        continue;
                    } else {
                        switchCommand(command);
                    }
                } catch (MyException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.print("$ ");
        }
    }

    private static void packageMode(String[] args) {
        LinkedHashSet<String> com = new LinkedHashSet<String>();

        String s = "";
        for (String string : args) {

            s = s + string + " ";

        }
        String[] commands = s.split(";");

        for (String string : commands) {
            String[] command = string.trim().split("\\s+");
            try {
                if (command.length == 1 && command[0].length() == 0) {
                    continue;
                } else {
                    switchCommand(command);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
