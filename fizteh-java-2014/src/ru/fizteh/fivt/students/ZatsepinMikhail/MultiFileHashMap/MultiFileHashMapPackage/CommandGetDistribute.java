package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.FileMapPackage.Get;

public class CommandGetDistribute extends CommandMultiFileHashMap {
    public CommandGetDistribute() {
        name = "get";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }

        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        Get commandGet = new Get();
        return commandGet.run(currentTable, args);
    }
}
