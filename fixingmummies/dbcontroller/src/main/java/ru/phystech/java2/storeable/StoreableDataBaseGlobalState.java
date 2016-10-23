package ru.phystech.java2.storeable;


import org.apache.log4j.Logger;
import ru.phystech.java2.structured.Storeable;
import ru.phystech.java2.structured.Table;
import ru.phystech.java2.structured.TableProvider;
import ru.phystech.java2.tableimpl.MultiFileDataBaseGlobalState;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


public class StoreableDataBaseGlobalState extends MultiFileDataBaseGlobalState {
    private static Logger logger = Logger.getLogger(StoreableDataBaseGlobalState.class);
    public Table currentTable = null;
    public TableProvider currentTableManager = null;
    public boolean autoCommitOnExit;


    public StoreableDataBaseGlobalState(TableProvider tableManager) {
        currentTableManager = tableManager;
        autoCommitOnExit = false;
    }

    @Override
    public String getCurrentTable() {
        if (currentTable == null) {
            return null;
        } else {
            return currentTable.getName();
        }
    }

    @Override
    public void setCurrentTable(String useTableAsCurrentName) {
        currentTable = currentTableManager.getTable(useTableAsCurrentName);
    }

    @Override
    public String put(String key, String value) {
        logger.info("get " + key + value);
        Storeable toPut = null;
        try {
            toPut = currentTableManager.deserialize(currentTable, value);
        } catch (ParseException exc) {
            logger.error("data base global state: storeable problems", exc);
        }

        Storeable resultStoreable = currentTable.put(key, toPut);
        if (resultStoreable == null) {
            return null;
        } else {
            return currentTableManager.serialize(currentTable, resultStoreable);
        }
    }

    @Override
    public String remove(String key) {
        logger.info("remove " + key);
        Storeable toRemove = currentTable.remove(key);
        if (toRemove == null) {
            return null;
        } else {
            return currentTableManager.serialize(currentTable, toRemove);
        }
    }

    @Override
    public String get(String key) {
        logger.info("get " + key);
        Storeable toGet = currentTable.get(key);
        if (toGet == null) {
            return null;
        } else {
            return currentTableManager.serialize(currentTable, toGet);
        }
    }

    @Override
    public int commit() throws IOException {
        logger.info("commit");
        return currentTable.commit();
    }

    @Override
    public int rollback() {
        logger.info("rollback");
        return currentTable.rollback();
    }

    @Override
    public int size() {
        return currentTable.size();
    }

    @Override
    public int amountOfChanges() {
        return ((StoreableTable) currentTable).getAmountOfChanges();
    }

    @Override
    public boolean isTableExist(String tableName) {
        return (getStoreableTable(tableName) != null);
    }

    public Table getStoreableTable(String tableName) {
        return currentTableManager.getTable(tableName);
    }

    @Override
    public void removeTable(String tableName) {
        if (currentTable != null) {
            if (currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
        }
        try {
            currentTableManager.removeTable(tableName);
        } catch (IOException exc) {
            logger.error("BOOM!111", exc);
            System.err.println(exc.getMessage());
        }
    }

    @Override
    public void createTable(List<String> args) {
        String useTableName = args.get(1);
        if (getStoreableTable(useTableName) != null) {
            System.err.println(useTableName + " exists");
        } else {
            try {
                List<Class<?>> columnTypes = StoreableTableProviderFactory.WorkWithStoreableDataBase.createListOfTypes(args);
                currentTableManager.createTable(useTableName, columnTypes);
                System.out.println("created");
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
                logger.error("BOOM!!", exc);
            }
        }
    }
}
