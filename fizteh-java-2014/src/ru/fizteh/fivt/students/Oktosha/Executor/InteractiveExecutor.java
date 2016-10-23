package ru.fizteh.fivt.students.Oktosha.Executor;

import ru.fizteh.fivt.students.Oktosha.Command.Command;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtility;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtilityException;

import java.util.Scanner;

/**
 * InteractiveExecutor is a child of Executor
 * which runs ConsoleUtility in interactive mode
 */
public class InteractiveExecutor extends Executor {
    public static void execute(ConsoleUtility utility) {
        Scanner sc = new Scanner(System.in);
        System.out.print("$ ");
        System.out.flush();
        while (sc.hasNextLine()) {
            String commandsString = sc.nextLine();
            try {
                Command[] commands = parse(commandsString);
                for (Command cmd : commands) {
                    utility.run(cmd);
                }
            } catch (ConsoleUtilityException e) {
                System.err.println(e.getMessage());
                System.err.flush();
                System.out.flush();
            } finally {
                System.out.print("$ ");
                System.out.flush();
                System.err.flush();
            }
        }
    }
}
