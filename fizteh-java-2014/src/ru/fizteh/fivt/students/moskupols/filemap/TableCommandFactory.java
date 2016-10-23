package ru.fizteh.fivt.students.moskupols.filemap;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

/**
 * Created by moskupols on 28.09.14.
 */
public class TableCommandFactory implements CommandFactory {
    private final DataBaseTable db;

    public TableCommandFactory(DataBaseTable db) {
        this.db = db;
    }

    @Override
    public Command fromString(String s) throws UnknownCommandException {
        final String[] argv = s.trim().split("\\s+");

        switch (argv[0]) {
            case "put":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        if (argv.length != 3) {
                            throw new CommandExecutionException(this, "Unexpected number of arguments");
                        }
                        String old = db.map.put(argv[1], argv[2]);
                        if (null == old) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(old);
                        }
                    }

                    @Override
                    public String name() {
                        return "put";
                    }
                };

            case "get":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        if (argv.length != 2) {
                            throw new CommandExecutionException(this, "Unexpected number of arguments");
                        }
                        String value = db.map.get(argv[1]);
                        System.out.println(value == null ? "not found" : value);
                    }

                    @Override
                    public String name() {
                        return "get";
                    }
                };

            case "remove":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        if (argv.length != 2) {
                            throw new CommandExecutionException(this, "Unexpected number of arguments");
                        }
                        System.out.println(db.map.remove(argv[1]) == null ? "not found" : "removed");
                    }

                    @Override
                    public String name() {
                        return "remove";
                    }
                };

            case "list":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        if (argv.length != 1) {
                            throw new CommandExecutionException(this, "Unexpected number of arguments");
                        }
                        for (String k : db.map.keySet()) {
                            System.out.println(k);
                        }
                    }

                    @Override
                    public String name() {
                        return "list";
                    }
                };

            case "exit":
                return new Command() {
                    @Override
                    public void execute() throws CommandExecutionException, StopProcessingException {
                        if (argv.length != 1) {
                            throw new CommandExecutionException(this, "Unexpected number of arguments");
                        }
                        throw new StopProcessingException();
                    }

                    @Override
                    public String name() {
                        return "exit";
                    }
                };

            default:
                throw new UnknownCommandException(argv[0]);
        }
    }
}
