package ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.DBTable;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.MultiFileHashMap;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.Command;

import java.io.File;
import java.util.ArrayList;

public class CommandShow extends Command {
    private MultiFileHashMap multiFileHashMap;

    public void execute(ArrayList<String> args) throws Exception {
        if (args.size() == 0) {
            throw new Exception("missing operand");
        }

        String showingData = args.get(0);

        try {
            if (showingData.equals("tables")) {
                if (multiFileHashMap.getWorkingDBTable() != null) {
                    multiFileHashMap.getWorkingDBTable().writeToFile();
                }

                for (File file : multiFileHashMap.getWorkingDirectory().listFiles()) {
                    DBTable dbTable = new DBTable(file);
                    AbstractShell.printInformation(dbTable.getWorkingDirectory().getName()
                            + " " + dbTable.getNumberOfItems());
                }
            }
        } catch (Exception e) {
            throw new Exception("invalid tables in working directory");
        }

    }

    public CommandShow(MultiFileHashMap fileMap) {
        multiFileHashMap = fileMap;
    }

}
