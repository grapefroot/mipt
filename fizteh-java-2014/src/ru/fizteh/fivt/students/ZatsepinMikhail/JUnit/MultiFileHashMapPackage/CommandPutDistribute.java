package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage.FmCommandPut;

public class CommandPutDistribute extends CommandMultiFileHashMap {
    public CommandPutDistribute() {
        name = "put";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        FmCommandPut commandPut = new FmCommandPut();
        return commandPut.run(currentTable, args);
    }
}
