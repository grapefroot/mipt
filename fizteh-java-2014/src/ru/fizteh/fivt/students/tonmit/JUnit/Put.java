package ru.fizteh.fivt.students.tonmit.JUnit;

import java.io.File;

public class Put extends JUnitCommand {

    public Put() {
        super("put", 2);
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
        String value = dbConnector.activeTable.put(args[0], args[1]);
        if (value != null) {
            System.out.println("overwrite");
            System.out.println(value);
        } else {
            System.out.println("new");
        }
        String newPath = dbConnector.activeTable.whereToSave("", args[0]).getKey();
        if (new File(newPath).exists() || dbConnector.activeTable.changedFiles.containsKey(newPath)) {
            Integer collisionCount = dbConnector.activeTable.changedFiles.get(newPath);
            if (collisionCount == null) {
                collisionCount = dbConnector.activeTable.countOfCollisionsInFile(new File(newPath).toPath());
            }
            ++collisionCount;
            dbConnector.activeTable.changedFiles.put(newPath, collisionCount);
        } else {
            dbConnector.activeTable.changedFiles.put(newPath, 0);
        }
        return true;
    }
}
