package ru.fizteh.fivt.students.dnovikov.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

public class DataBaseTable implements Table {
    private static final int FILES_COUNT = 16;
    private static final int FOLDERS_COUNT = 16;
    private String tableName;
    private List<SingleTable> tableParts;
    private SingleTable[][] parts;
    private DataBaseProvider databaseConnector;
    private Map<String, String> diffs;

    public DataBaseTable(String name, DataBaseProvider dbConnector) {
        diffs = new HashMap<>();
        tableName = new String(name);
        tableParts = new ArrayList<>();
        databaseConnector = dbConnector;
        parts = new SingleTable[FOLDERS_COUNT][];
        for (int i = 0; i < FOLDERS_COUNT; ++i) {
            parts[i] = new SingleTable[FILES_COUNT];
        }
        for (int i = 0; i < FOLDERS_COUNT; ++i) {
            for (int j = 0; j < FILES_COUNT; ++j) {
                parts[i][j] = new SingleTable(i, j, this);
                tableParts.add(parts[i][j]);
            }
        }
    }

    private SingleTable selectSingleTable(String key) {
        int hashCode = key.hashCode();
        int directoryNumber = hashCode % FOLDERS_COUNT;
        int fileNumber = hashCode / FOLDERS_COUNT % FILES_COUNT;
        if (directoryNumber < 0) {
            directoryNumber += FOLDERS_COUNT;
        }
        if (fileNumber < 0) {
            fileNumber += FILES_COUNT;
        }
        return parts[directoryNumber][fileNumber];
    }

    @Override
    public List<String> list() {
        List<String> result = new ArrayList<>();
        Set<String> setResult = new HashSet<>();
        for (SingleTable table : tableParts) {
            List<String> keys = table.list();
            setResult.addAll(keys);
        }
        for (Entry<String, String> pair : diffs.entrySet()) {
            if (pair.getValue() == null) {
                setResult.remove(pair.getKey());
            } else {
                setResult.add(pair.getKey());
            }
        }
        result.addAll(setResult);
        return result;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("error of get: key is null");
        }
        String value;
        SingleTable table = selectSingleTable(key);
        if (diffs.containsKey(key)) {
            value = diffs.get(key);
        } else {
            value = table.get(key);
        }
        return value;
    }

    public Path getTableDirectory() {
        return databaseConnector.getRootDirectory().resolve(tableName);
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("error of put: key or value is null");
        }
        SingleTable table = selectSingleTable(key);
        String oldValue;
        if (!diffs.containsKey(key)) {
            oldValue = table.get(key);
            if (oldValue != null && oldValue.equals(value)) {
                return oldValue;
            }
        } else {
            oldValue = diffs.remove(key);
        }
        diffs.put(key, value);
        return oldValue;
    }

    @Override
    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("error of remove: key is null");
        }
        SingleTable table = selectSingleTable(key);

        String deleted;
        if (!diffs.containsKey(key)) {
            deleted = table.get(key);
            diffs.put(key, null);
        } else {
            deleted = diffs.remove(key);
            if (table.get(key) != null) {
                diffs.put(key, null);
            }
        }
        return deleted;
    }

    public void drop() {
        File tableDirectory = getTableDirectory().toFile();
        for (SingleTable[] directory : parts) {
            for (SingleTable singleTableFile : directory) {
                singleTableFile.drop();
            }
        }
        tableDirectory.delete();
    }

    @Override
    public int size() {
        return list().size();
    }

    @Override
    public int commit() {
        int changesCount = diffs.size();
        for (Entry<String, String> entryDiff : diffs.entrySet()) {
            SingleTable table = selectSingleTable(entryDiff.getKey());
            if (entryDiff.getValue() == null) {
                table.remove(entryDiff.getKey());
            } else {
                table.put(entryDiff.getKey(), entryDiff.getValue());
            }
        }
        diffs.clear();
        save();
        return changesCount;
    }

    @Override
    public int rollback() {
        int changesCount = diffs.size();
        diffs.clear();
        return changesCount;
    }

    public void save() {
        for (SingleTable singleTable : tableParts) {
            singleTable.save();
        }
    }

    public int getNumberOfChanges() {
        return diffs.size();
    }
}



