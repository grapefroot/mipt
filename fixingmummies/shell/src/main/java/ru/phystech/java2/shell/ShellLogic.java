package ru.phystech.java2.shell;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import ru.phystech.java2.table.Command;
import ru.phystech.java2.utils.MultiFileCmdParseAndExecute;
import ru.phystech.java2.utils.StoreableCmdParseAndExecute;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ShellLogic {

    private static final Logger logger = Logger.getLogger(ShellLogic.class);

    @Autowired
    private GenericCmdList cmdList;

    private static List<String> parseCmdAndArgs(String inputLine, final Integer parserAndExecutor) throws IOException {
        if (parserAndExecutor == 1) {
            return MultiFileCmdParseAndExecute.intoCommandsAndArgs(inputLine, ";");
        } else if (parserAndExecutor == 2) {
            return StoreableCmdParseAndExecute.splitByDelimiter(inputLine, ";");
        } else {
            throw new IOException("package mode: parser and executor fail, can not start suitable method");
        }
    }

    private void execute(String command, final Integer parserAndExecutor)
            throws IOException {
        if (parserAndExecutor == 1) {
            MultiFileCmdParseAndExecute.execute(command, cmdList.getCmdList());
        } else if (parserAndExecutor == 2) {
            StoreableCmdParseAndExecute.execute(command, cmdList.getCmdList());
        } else {
            throw new IOException("package mode: parser and executor fail, can not start suitable method");
        }
    }

    public void packageMode(String[] args, PrintStream out, PrintStream err,
                            final Integer parserAndExecutor) {
        StringBuilder packOfCommands = new StringBuilder();
        for (String cmdOrArg : args) {
            packOfCommands.append(cmdOrArg).append(" ");
        }
        String inputLine = packOfCommands.toString();
        try {
            List<String> commandWithArgs = parseCmdAndArgs(inputLine, parserAndExecutor);
            for (String command : commandWithArgs) {
                execute(command, parserAndExecutor);
            }
        } catch (Exception exc) {
            logger.fatal("BOOM!111", exc);
            System.exit(3);
        }
    }


    public void interactiveMode(InputStream in, PrintStream out, PrintStream err,
                                final Integer parserAndExecutor) {
        Scanner inputStream = new Scanner(in);
        do {
            out.flush();
            err.flush();
            out.print("$ ");
            String inputLine = inputStream.nextLine();
            try {
                List<String> commandWithArgs = parseCmdAndArgs(inputLine, parserAndExecutor);
                for (String command : commandWithArgs) {
                    out.flush();
                    err.flush();
                    execute(command, parserAndExecutor);
                }
            } catch (Exception exc) {
                logger.error("OKAY!1", exc);
            }
        } while (!Thread.currentThread().isInterrupted());
    }
}
