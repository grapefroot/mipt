package ru.fizteh.fivt.students.tonmit.JUnit;

public class Remove extends JUnitCommand {
    public Remove() {
        super("remove", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.activeTable == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        if (dbConnector.activeTable.remove(args[0]) != null) {
            System.out.println("removed");
            dbConnector.activeTable.changedFiles.put(
                    dbConnector.activeTable.whereToSave("", args[0]).getKey(), 0);
        } else {
            System.err.println("not found");
            if (batchModeInInteractive) {
                return false;
            }
        }
        return true;
    }
}
