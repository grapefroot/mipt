package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

import java.io.File;

public class MultiPut extends Command {

    private String key;
    private String value;

    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }

    public MultiPut(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    public MultiPut() {}

    protected int numberOfArguments() {
        return 2;
    }

    @Override
    public void executeOnTable(Table table) throws Exception {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % ConstClass.NUM_DIRECTORIES;
        int file = hashCode / ConstClass.NUM_FILES % ConstClass.NUM_FILES;
        Put put = new Put(key, value);
        if (table.getClass() == Table.class) {
            if (table.databases[dir][file] == null) {
                File subDir = new File(table.mainDir, String.valueOf(dir) + ".dir");
                if (!subDir.exists()) {
                    if (!subDir.mkdir()) {
                        throw new Exception("Unable to create directories in working catalog");
                    }
                }
                File dbFile = new File(subDir, String.valueOf(file) + ".dat");
                if (!dbFile.exists() && !dbFile.createNewFile()) {
                    throw new Exception("Unable to create database files in working catalog");
                }
                table.databases[dir][file] = new DataBase(dbFile.toString());
            }
        }
        put.execute(table.databases[dir][file]);
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            executeOnTable(base.getUsing());
        }
    }
}
