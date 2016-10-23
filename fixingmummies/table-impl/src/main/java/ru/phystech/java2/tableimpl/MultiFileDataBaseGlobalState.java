package ru.phystech.java2.tableimpl;

import org.springframework.stereotype.Service;
import ru.phystech.java2.table.Table;
import ru.phystech.java2.table.TableProvider;

import java.util.List;

@Service
public class MultiFileDataBaseGlobalState extends FileMapGlobalState {
    public boolean autoCommitOnExit;
    private TableProvider currentTableManager = null;

    public MultiFileDataBaseGlobalState() {
    }

    public MultiFileDataBaseGlobalState(TableProvider tableManager) {
        currentTableManager = tableManager;
        autoCommitOnExit = false;
    }


    public void setCurrentTable(String useTableAsCurrentName) {
        currentTable = currentTableManager.getTable(useTableAsCurrentName);
    }

    public int amountOfChanges() {
        return ((MultiFileTable) currentTable).getAmountOfChanges();
    }


    public boolean isTableExist(String tableName) {
        return (getMultiFileTable(tableName) != null);
    }

    public Table getMultiFileTable(String tableName) {
        return currentTableManager.getTable(tableName);
    }

    public void removeTable(String tableName) {
        if (currentTable != null) {
            if (currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
        }

        currentTableManager.removeTable(tableName);

    }

    public void createTable(List<String> args) {
        String useTableName = args.get(1);
        if (getMultiFileTable(useTableName) != null) {
            System.err.println(useTableName + " exists");
        } else {
            currentTableManager.createTable(useTableName);
            System.out.println("created");
        }
    }
}
