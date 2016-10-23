package ru.phystech.java2.tableimpl;


import org.apache.log4j.Logger;
import ru.phystech.java2.table.Table;
import ru.phystech.java2.utils.CheckOnCorrect;
import ru.phystech.java2.utils.CountingTools;
import ru.phystech.java2.utils.WorkWithDirs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiFileTable implements Table {
    private static Logger logger = Logger.getLogger(MultiFileTable.class);
    private File tableStorageDirectory;
    private Map<String, String> data = new HashMap<String, String>();
    private Map<String, String> changes = new HashMap<String, String>();
    private Set<String> removedKeys = new HashSet<String>();
    private int amountOfChanges = 0;

    public MultiFileTable(File dataDirectory) {
        tableStorageDirectory = dataDirectory;
        try {
            WorkWithDirs.readIntoDataBase(tableStorageDirectory, data);
        } catch (IOException exc) {
            throw new IllegalArgumentException("Read from file failed", exc);
        }
    }

    @Override
    public String getName() {
        return tableStorageDirectory.getName();
    }

    @Override
    public String get(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("get: key is bad");
        }
        String resultOfGet = changes.get(key);
        if (resultOfGet == null) {
            if (removedKeys.contains(key)) {
                return null;
            }
            resultOfGet = data.get(key);
        }
        return resultOfGet;
    }

    @Override
    public String put(String key, String value) {
        if (!CheckOnCorrect.goodArg(key) || !CheckOnCorrect.goodArg(value)) {
            throw new IllegalArgumentException("put: key or value is bad");
        }
        String valueInData = data.get(key);
        String resultOfPut = changes.put(key, value);

        if (resultOfPut == null) {
            amountOfChanges++;
            if (!removedKeys.contains(key)) {
                resultOfPut = valueInData;
            }
        }
        if (valueInData != null) {
            removedKeys.add(key);
        }
        return resultOfPut;
    }

    @Override
    public String remove(String key) {
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("remove: key is null");
        }

        String resultOfRemove = changes.get(key);
        if (resultOfRemove == null && !removedKeys.contains(key)) {
            resultOfRemove = data.get(key);
        }
        if (changes.containsKey(key)) {
            amountOfChanges--;
            changes.remove(key);
            if (data.containsKey(key)) {
                removedKeys.add(key);
            }
        } else {
            if (data.containsKey(key) && !removedKeys.contains(key)) {
                removedKeys.add(key);
                amountOfChanges++;
            }
        }
        return resultOfRemove;
    }

    @Override
    public int size() {
        return data.size() + changes.size() - removedKeys.size();
    }

    @Override
    public int commit() {
        int result = CountingTools.correctCountingOfChanges(data, changes, removedKeys);
        for (String key : removedKeys) {
            data.remove(key);
        }
        data.putAll(changes);
        try {
            WorkWithDirs.writeIntoFiles(tableStorageDirectory, data);
        } catch (Exception exc) {
            System.err.println("commit: " + exc.getMessage());
            logger.error(exc.getMessage(), exc);
        }
        setDefault();
        return result;
    }

    @Override
    public int rollback() {
        int result = CountingTools.correctCountingOfChanges(data, changes, removedKeys);
        setDefault();
        return result;
    }

    private void setDefault() {
        changes.clear();
        removedKeys.clear();
        amountOfChanges = 0;
    }

    public int getAmountOfChanges() {
        return amountOfChanges;
    }
}
