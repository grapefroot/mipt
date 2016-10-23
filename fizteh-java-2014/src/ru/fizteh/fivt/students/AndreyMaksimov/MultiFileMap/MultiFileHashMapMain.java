package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;


public class MultiFileHashMapMain {
    public static void main(String[] arguments) {
        String needBasePath = System.getProperty("fizteh.db.dir");
        if (needBasePath == null) {
            System.err.println("Unfortunately no such path");
            System.exit(1);
        }
        try {
            DataBaseDir needDirectory = new DataBaseDir(needBasePath);
            boolean interactivemode = true;
            CommandMode mode;
            if (arguments.length == 0) {
                interactivemode = true;
                mode = new InteractiveMode(); //Возвращает массив;
            } else {
                mode = new BatchMode(arguments);
            }
            boolean exitStatus = false;
            do {
                try {
                    String s = mode.runInterpreterCycle();
                    Command command = Command.fromString(s);
                    command.startNeedMultiInstruction(needDirectory);
                } catch (ExitException e) {
                    exitStatus = true;
                } catch (Exception e) {
                    if (interactivemode) {
                        System.err.println(e.getMessage());
                        System.err.flush();
                    } else {
                        throw e;
                    }
                }
            } while (!exitStatus);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

