package ru.phystech.java2.storeable;

import org.apache.log4j.Logger;
import ru.phystech.java2.structured.ColumnFormatException;
import ru.phystech.java2.structured.Storeable;
import ru.phystech.java2.structured.Table;
import ru.phystech.java2.structured.TableProvider;
import ru.phystech.java2.utils.CheckOnCorrect;
import ru.phystech.java2.utils.CountingTools;
import ru.phystech.java2.utils.WorkStatus;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StoreableTable implements Table, AutoCloseable {

    private static Logger logger = Logger.getLogger(StoreableTable.class);
    private final Lock transactionLock = new ReentrantLock(true);
    private TableProvider provider;
    private File tableStorageDirectory;
    private List<Class<?>> columnTypes;
    private Map<String, Storeable> data;
    private ThreadLocal<WorkStatus> status = new ThreadLocal<>();
    private ThreadLocal<HashMap<String, Storeable>> changes;
    private ThreadLocal<HashSet<String>> removedKeys;
    private ThreadLocal<Integer> amountOfChanges;


    public StoreableTable(File dataDirectory, TableProvider givenProvider) throws IOException {
        data = new HashMap<>();
        status.set(WorkStatus.NOT_INITIALIZED);
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };

        removedKeys = new ThreadLocal<HashSet<String>>() {
            @Override
            public HashSet<String> initialValue() {
                return new HashSet<>();
            }
        };

        amountOfChanges = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return 0;
            }
        };

        if (givenProvider == null) {
            throw new IOException("ru.phystech.java2.storeable table: create failed, provider is not set");
        }
        provider = givenProvider;
        tableStorageDirectory = dataDirectory;
        try {
            status.set(WorkStatus.WORKING);
            StoreableTableProviderFactory.WorkWithStoreableDataBase.readIntoDataBase(tableStorageDirectory, data, this, provider);
        } catch (IOException | ParseException exc) {
            throw new IllegalArgumentException("Reading from file failed", exc);
        }
    }

    public StoreableTable(File dataDirectory, List<Class<?>> givenTypes, TableProvider givenProvider)
            throws IOException {
        if (givenProvider == null) {
            throw new IOException("table: creating failed, provider is not set");
        }

        data = new HashMap<>();
        status.set(WorkStatus.NOT_INITIALIZED);
        changes = new ThreadLocal<HashMap<String, Storeable>>() {
            @Override
            public HashMap<String, Storeable> initialValue() {
                return new HashMap<>();
            }
        };

        removedKeys = new ThreadLocal<HashSet<String>>() {
            @Override
            public HashSet<String> initialValue() {
                return new HashSet<>();
            }
        };

        amountOfChanges = new ThreadLocal<Integer>() {
            @Override
            public Integer initialValue() {
                return 0;
            }
        };

        provider = givenProvider;
        tableStorageDirectory = dataDirectory;
        columnTypes = givenTypes;
        status.set(WorkStatus.WORKING);
        StoreableTableProviderFactory.WorkWithStoreableDataBase.createSignatureFile(tableStorageDirectory, this);
    }

    @Override
    public String getName() {
        status.get().isOkForOperations();
        return tableStorageDirectory.getName();
    }

    @Override
    public Storeable get(String key) {
        status.get().isOkForOperations();
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("get: key is bad");
        }
        Storeable resultOfGet = changes.get().get(key);
        if (resultOfGet == null) {
            if (removedKeys.get().contains(key)) {
                return null;
            }
            transactionLock.lock();
            try {
                resultOfGet = data.get(key);
            } finally {
                transactionLock.unlock();
            }
        }
        return resultOfGet;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        status.get().isOkForOperations();
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("put: key is bad");
        }
        if (!CheckOnCorrect.goodStoreable(value, this.columnTypes)) {
            throw new ColumnFormatException("put: value not suitable for this table");
        }
        Storeable valueInData;
        transactionLock.lock();
        try {
            valueInData = data.get(key);
        } finally {
            transactionLock.unlock();
        }
        Storeable resultOfPut = changes.get().put(key, value);

        if (resultOfPut == null) {
            amountOfChanges.set(amountOfChanges.get() + 1);
            if (!removedKeys.get().contains(key)) {
                resultOfPut = valueInData;
            }
        }
        if (valueInData != null) {
            removedKeys.get().add(key);
        }
        return resultOfPut;
    }

    @Override
    public Storeable remove(String key) {
        status.get().isOkForOperations();
        if (!CheckOnCorrect.goodArg(key)) {
            throw new IllegalArgumentException("remove: key is bad");
        }
        Storeable resultOfRemove = changes.get().get(key);
        if (resultOfRemove == null && !removedKeys.get().contains(key)) {
            transactionLock.lock();
            try {
                resultOfRemove = data.get(key);
            } finally {
                transactionLock.unlock();
            }
        }
        if (changes.get().containsKey(key)) {
            amountOfChanges.set(amountOfChanges.get() - 1);
            changes.get().remove(key);
            transactionLock.lock();
            try {
                if (data.containsKey(key)) {
                    removedKeys.get().add(key);
                }
            } finally {
                transactionLock.unlock();
            }
        } else {
            transactionLock.lock();
            try {
                if (data.containsKey(key) && !removedKeys.get().contains(key)) {
                    removedKeys.get().add(key);
                    amountOfChanges.set(amountOfChanges.get() + 1);
                }
            } finally {
                transactionLock.unlock();
            }
        }
        return resultOfRemove;
    }

    @Override
    public int size() {
        status.get().isOkForOperations();
        transactionLock.lock();
        try {
            return CountingTools.correctCountingOfSize(data, changes.get(), removedKeys.get());
        } finally {
            transactionLock.unlock();
        }
    }

    @Override
    public int commit() {
        status.get().isOkForOperations();
        int result = -1;
        transactionLock.lock();
        try {
            result = CountingTools.correctCountingOfChangesInStoreable(this, data, changes.get(), removedKeys.get());
            for (String key : removedKeys.get()) {
                data.remove(key);
            }
            data.putAll(changes.get());
            try {
                StoreableTableProviderFactory.WorkWithStoreableDataBase.writeIntoFiles(tableStorageDirectory, data, this, provider);
            } catch (Exception exc) {
                System.err.println("commit: " + exc.getMessage());
            }
        } finally {
            transactionLock.unlock();
        }
        setDefault();
        return result;
    }

    @Override
    public int rollback() {
        status.get().isOkForOperations();
        int result = -1;
        transactionLock.lock();
        try {
            result = CountingTools.correctCountingOfChangesInStoreable(this, data, changes.get(), removedKeys.get());
        } finally {
            transactionLock.unlock();
        }
        setDefault();
        return result;
    }

    @Override
    public int getColumnsCount() {
        status.get().isOkForOperations();
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        status.get().isOkForOperations();
        int columnsCount = getColumnsCount();
        if (columnIndex < 0 || columnIndex > columnsCount - 1) {
            throw new IndexOutOfBoundsException("get column type: bad index");
        }
        return columnTypes.get(columnIndex);
    }

    public List<Class<?>> getColumnTypes() {
        status.get().isOkForOperations();
        return columnTypes;
    }

    public void setColumnTypes(List<Class<?>> givenColumnTypes) {
        columnTypes = givenColumnTypes;
    }

    @Override
    public String toString() {
        status.get().isOkForOperations();
        return getClass().getSimpleName() + "[" + tableStorageDirectory + "]";
    }

    @Override
    public void close() {
        status.get().isOkForClose();
        if (status.get() == WorkStatus.WORKING) {
            rollback();
        }
        status.set(WorkStatus.CLOSED);
    }

    private void setDefault() {
        changes.get().clear();
        removedKeys.get().clear();
        amountOfChanges.set(0);
    }

    public boolean isOkForOperations() {
        try {
            status.get().isOkForOperations();
        } catch (IllegalStateException exc) {
            return false;
        }
        return true;
    }

    public int getAmountOfChanges() {
        status.get().isOkForOperations();
        return amountOfChanges.get();
    }
}
