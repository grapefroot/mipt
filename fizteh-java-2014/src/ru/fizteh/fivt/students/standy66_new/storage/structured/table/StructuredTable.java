package ru.fizteh.fivt.students.standy66_new.storage.structured.table;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredTable implements Table {
    private StringTable backendTable;
    private StructuredDatabase database;
    private TableSignature tableSignature;


    public StructuredTable(StringTable backendTable, StructuredDatabase database) {
        this.backendTable = backendTable;
        this.database = database;
        File signatureFile = new File(backendTable.getFile(), "signature.tsv");
        try {
            tableSignature = TableSignature.readFromFile(signatureFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("signature.tsv for table "
                    + backendTable.getName() + " doesn't exist", e);
        }
    }

    public TableSignature getTableSignature() {
        return tableSignature;
    }

    public StringTable getBackendTable() {
        return backendTable;
    }

    @Override
    public synchronized TableRow put(String key, Storeable value) throws ColumnFormatException {
        TableRow oldValue = get(key);
        backendTable.put(key, TableRow.fromStoreable(tableSignature, value).serialize());
        return oldValue;
    }

    @Override
    public synchronized TableRow remove(String key) {
        TableRow value = get(key);
        backendTable.remove(key);
        return value;
    }

    @Override
    public int size() {
        return backendTable.size();
    }

    @Override
    public int commit() throws IOException {
        return backendTable.commit();
    }

    @Override
    public int rollback() {
        return backendTable.rollback();
    }

    @Override
    public int getColumnsCount() {
        return tableSignature.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return tableSignature.getClassAt(columnIndex);
    }

    @Override
    public String getName() {
        return backendTable.getName();
    }

    @Override
    public List<String> list() {
        return backendTable.list();
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return backendTable.unsavedChangesCount();
    }

    @Override
    public synchronized TableRow get(String key) {
        String value = backendTable.get(key);
        if (value == null) {
            return null;
        }
        try {
            return database.deserialize(this, value);
        } catch (ParseException e) {
            throw new RuntimeException("ParseException occurred", e);
        }
    }
}
