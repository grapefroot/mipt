package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage;

public class FmCommandPut extends CommandFileMap {
    public FmCommandPut() {
        name = "put";
        numberOfArguments = 3;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        String oldValue = myFileMap.put(args[1], args[2]);
        if (oldValue != null) {
            System.out.println("overwrite\n" + oldValue);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
