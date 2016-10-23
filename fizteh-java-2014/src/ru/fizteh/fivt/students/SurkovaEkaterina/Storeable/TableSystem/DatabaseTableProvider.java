package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TypesParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.XmlOperations.XmlDeserializer;
import ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.XmlOperations.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseTableProvider implements TableProvider {

    static final String SIGNATURE_FILE = "signature.tsv";
    private static final int MAX_TABLES_NUMBER = 16;
    private String databaseDirectoryPath;
    private DatabaseTable currentTable = null;
    HashMap<String, DatabaseTable> tables =
            new HashMap<String, DatabaseTable>();

    public DatabaseTableProvider(String directory) {
        if ((directory == null)
                || (directory.equals(""))) {
            throw new IllegalArgumentException(
                    "Directory's name can not be empty!");
        }

        this.databaseDirectoryPath = directory;
        File databaseDirectory = new File(directory);

        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "Directory should not be a file!");
        }

        this.databaseDirectoryPath =
                databaseDirectory.getAbsolutePath();
        databaseDirectory = new File(databaseDirectoryPath);
        for (final File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }

            List<Class<?>> columnTypes = readTableSignature(tableFile.getName());

            if (columnTypes == null) {
                throw new IllegalArgumentException("Table does not exist!");
            }

            DatabaseTable table = new DatabaseTable(this, databaseDirectoryPath, tableFile.getName(), columnTypes);
            tables.put(table.getName(), table);
        }
    }

    @Override
    public List<String> getTableNames() {
        List<String> names = new ArrayList<String>();
        for (String name : tables.keySet()) {
            names.add(name);
        }
        return names;
    }

    @Override
    public Table getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Table's name can not be empty!");
        }

        DatabaseTable table = tables.get(name);

        if (table == null) {
            return table;
        }

        currentTable = table;
        return table;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Table's name cannot be null!");
        }

        checkTableName(name);

        if (columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Column types cannot be null!");
        }

        checkColumnTypes(columnTypes);

        if (tables.containsKey(name)) {
            return null;
        }

        DatabaseTable table = new DatabaseTable(this, databaseDirectoryPath, name, columnTypes);
        tables.put(name, table);
        return table;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        XmlDeserializer deserializer = new XmlDeserializer(value);
        Storeable result = null;
        List<Object> values = new ArrayList<Object>(table.getColumnsCount());

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            try {
                Class<?> expectedType = table.getColumnType(index);
                Object columnValue = deserializer.getNext(expectedType);
                values.add(columnValue);
            } catch (ColumnFormatException e) {
                throw new ParseException("Incompatible type: " + e.getMessage(), index);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Xml representation doesn't match the !", index);
            }
        }

        try {
            deserializer.close();
            result = createFor(table, values);
        } catch (ColumnFormatException e) {
            throw new ParseException("Incompatible types: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Xml representation doesn't match the format!", 0);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        return result;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null!");
        }

        try {
            XmlSerializer xmlSerializer = new XmlSerializer();
            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(value.getColumnAt(index));
            }
            xmlSerializer.close();
            return xmlSerializer.getRepresentation();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Incorrect value!");
        }
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return createRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException("Values list cannot be empty!");
        }

        DatabaseTableRow row = createRow(table);
        row.setColumns(values);

        return row;
    }

    private DatabaseTableRow createRow(Table table) {
        DatabaseTableRow row = new DatabaseTableRow();

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            row.addColumn(table.getColumnType(index));
        }

        return row;
    }

    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }

        if (fileToDelete.isDirectory()) {
            for (final File file : fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }

        fileToDelete.delete();
    }

    public void removeTable(String name) {
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException(
                    "Table's name cannot be empty!");
        }

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(
                    String.format("%s not exists!", name));
        }

        tables.remove(name);

        File tableFile = new File(databaseDirectoryPath, name);
        deleteFile(tableFile);
    }

    public void showTables() {
        for (final Map.Entry<String, DatabaseTable> map: tables.entrySet()) {
            System.out.println(map.getKey() + ' ' + map.getValue().size());
        }
    }

    public final void exit() throws IOException {
        if (currentTable != null) {
            currentTable.save();
        }
    }

    private List<Class<?>> readTableSignature(String tableName) {
        File tableDirectory = new File(databaseDirectoryPath, tableName);
        File signatureFile = new File(tableDirectory, SIGNATURE_FILE);
        String signature = null;
        if (!signatureFile.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(signatureFile));
            signature = reader.readLine();
        } catch (IOException e) {
            System.err.println("Error loading signature file: " + e.getMessage());
            return null;
        }

        if (signature == null) {
            throw new IllegalArgumentException("Incorrect signature file!");
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        for (final String columnType : signature.split("\\s+")) {
            Class<?> type = TypesParser.getTypeByName(columnType);
            if (type == null) {
                throw new IllegalArgumentException("Unknown type!");
            }
            columnTypes.add(type);
        }
        return columnTypes;
    }

    private void checkColumnTypes(List<Class<?>> columnTypes) {
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null) {
                throw new IllegalArgumentException("Unknown column type!");
            }
            TypesParser.getNameByType(columnType);
        }
    }

    private void checkTableName(String name) {
        if (!name.matches("[0-9A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException("Bad symbol!");
        }
    }

    public void clear() {
        tables.clear();
    }
}
