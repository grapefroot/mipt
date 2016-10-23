package ru.fizteh.fivt.students.andreyzakharov.filemap;

public class PutCommand implements Command {
    @Override
    public String execute(DbConnector connector, String... args) throws CommandInterruptException {
        if (args.length < 3) {
            throw new CommandInterruptException("put: too few arguments");
        }
        if (args.length > 3) {
            throw new CommandInterruptException("put: too many arguments");
        }
        String value = connector.db.get(args[1]);
        connector.db.put(args[1], args[2]);
        try {
            connector.db.unload();
        } catch (ConnectionInterruptException e) {
            throw new CommandInterruptException("put: " + e.getMessage());
        }
        return value == null ? "new" : "overwrite\n" + value;
    }
}
