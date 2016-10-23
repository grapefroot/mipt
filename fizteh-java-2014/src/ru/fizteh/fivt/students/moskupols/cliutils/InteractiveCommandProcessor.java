package ru.fizteh.fivt.students.moskupols.cliutils;

import java.util.Scanner;

/**
 * Created by moskupols on 28.09.14.
 */
public class InteractiveCommandProcessor implements CommandProcessor {
    private final Object prompt;

    public InteractiveCommandProcessor(Object prompt) {
        this.prompt = prompt;
    }

    @Override
    public void process(CommandFactory commandFactory) {
        Scanner scanner = new Scanner(System.in);
        boolean exited = false;
        do {
            System.err.flush();
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                break;
            }

            for (String s : scanner.nextLine().split(";")) {
                try {
                    commandFactory.fromString(s).execute();
                } catch (UnknownCommandException | CommandExecutionException e) {
                    System.err.println(e.getMessage());
                    // e.printStackTrace();
                } catch (StopProcessingException e) {
                    exited = true;
                }
                if (exited) {
                    break;
                }
            }
        } while (!exited);
    }
}
