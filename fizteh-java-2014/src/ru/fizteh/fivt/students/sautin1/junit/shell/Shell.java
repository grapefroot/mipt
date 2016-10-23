package ru.fizteh.fivt.students.sautin1.junit.shell;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents Unix shell with interactive and non-interactive modes available.
 * Created by sautin1 on 9/30/14.
 */
public class Shell<T> {
    private final Map<String, Command<T>> commandsMap = new HashMap<>();
    private static final String PROMPT = "$ ";
    private T state;
    private CommandParser parser;

    public Shell(T state, Command<T>[] commands, CommandParser parser) {
        this.state = state;
        this.parser = parser;
        for (Command<T> command : commands) {
            commandsMap.put(command.toString(), command);
        }
    }

    public Shell(T state, Command<T>[] commands) {
        this(state, commands, new ShellCommandParser());
    }

    /**
     * Tries executing command with parameters commandWithParams.
     * @param commandWithParams - Array of command parameters. Zero-element is a command.
     * @throws UserInterruptException if user wants to exit.
     * @throws ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException if any error occurs.
     */
    public void executeCommand(String... commandWithParams) throws UserInterruptException, CommandExecuteException {
        Command<T> command = commandsMap.get(commandWithParams[0]);
        if (command == null) {
            throw new CommandExecuteException(commandWithParams[0] + ": command not found");
        }
        command.execute(state, commandWithParams);
    }

    /**
     * Parses string newCommand into commands and tries executing them.
     * @param newCommand - String consisting of one or a few commands.
     * @throws ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException if user wants to exit.
     * @throws ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException if any error occurs.
     */
    public void callCommands(String newCommand) throws UserInterruptException, CommandExecuteException {
        String[] commandArray = parser.splitStringIntoCommands(newCommand);

        for (String command : commandArray) {
            if (command.isEmpty()) {
                continue;
            }
            String[] params = parser.splitCommandIntoParams(command);
            executeCommand(params);
        }
    }

    /**
     * Asks user to enter commands and executes them.
     * @throws UserInterruptException if user wants to exit.
     * @throws ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException if any error occurs.
     */
    public void interactWithUser() throws UserInterruptException, CommandExecuteException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print(PROMPT);
                String newCommand = null;
                if (scanner.hasNextLine()) {
                    newCommand = scanner.nextLine().trim();
                } else {
                    return;
                }
                try {
                    callCommands(newCommand);
                } catch (CommandExecuteException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * Calls interactWithUser() in interactive mode or callCommands() in non-interactive mode.
     * @param args - arguments for non-interactive mode.
     * @throws ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException if user wants to exit.
     * @throws ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException if any error occurs.
     */
    public void startWork(String[] args) throws UserInterruptException, CommandExecuteException {
        if (args.length == 0) {
            // interactive mode
            interactWithUser();
        } else {
            // non-interactive mode
            callCommands(parser.convertArrayToString(args));
        }
    }
}
