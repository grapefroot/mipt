package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.AbstractStoreable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.shell.FileUtils;

public class MFileHashMap implements TableProvider {
    private String dataBaseDirectory;
    private HashMap<String, FileMap> tables;
    private FileMap currentTable;
    public MFileHashMap(String newDirectory) throws IOException {
        dataBaseDirectory = newDirectory;
        tables = new HashMap<>();
        init();
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalArgumentException {
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("null argument");
        }
        TypesUtils.checkTypes(columnTypes);
        if (tables.containsKey(name)) {
            return null;
        } else {
            Path pathOfNewTable = Paths.get(dataBaseDirectory, name);
            Path pathOfNewTableSignatureFile = Paths.get(dataBaseDirectory, name, "signature.tsv");
            if (Files.exists(pathOfNewTable) & Files.isDirectory(pathOfNewTable)) {
                throw new IllegalArgumentException("this directory already exists");
            }
            try {
                Files.createDirectory(pathOfNewTable);
                Files.createFile(pathOfNewTableSignatureFile);
                try (FileWriter fileOut = new FileWriter(pathOfNewTableSignatureFile.toString())) {
                    fileOut.write(TypesUtils.toFileSignature(columnTypes));
                }
                FileMap newTable = new FileMap(pathOfNewTable.toString(), columnTypes, this);
                tables.put(name, newTable);
                return newTable;
            } catch (IOException e) {
                throw new IOException();
            }
        }
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException, IOException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (tables.containsKey(name)) {
            Path pathForRemoveTable = Paths.get(dataBaseDirectory, name);
            tables.remove(name);
            currentTable = null;
            FileUtils.rmdir(pathForRemoveTable);
        } else {
            throw new IllegalStateException("table \'" + name + "\' doesn't exist");
        }
    }

    @Override
    public Storeable createFor(Table table) {
        Object[] startValues = new Object[table.getColumnsCount()];
        return new AbstractStoreable(startValues, table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException("number of types");
        }
        List<Object> objValues = new ArrayList<>(values);
        List<Class<?>> typeList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (objValues.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("mismatch column type");
            }
            typeList.add(table.getColumnType(i));
        }
        TypesUtils.checkNewStorableValue(typeList, objValues);
        return new AbstractStoreable(objValues.toArray(), table);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        if (table.getColumnsCount() != TypesUtils.getSizeOfStoreable(value)) {
            throw new ColumnFormatException("wrong size");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (!table.getColumnType(i).equals(value.getColumnAt(i).getClass())) {
                throw new ColumnFormatException("need: " + table.getColumnType(i)
                    + ", but got:" + value.getColumnAt(i).getClass());
            }
        }
        return Serializator.serialize(table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return Serializator.deserialize(table, value);
    }

    @Override
    public List<String> getTableNames() {
        List<String> result = new ArrayList<>();
        Collection<FileMap> filemaps = tables.values();
        for (FileMap oneTable : filemaps) {
            result.add(oneTable.getName());
        }
        return result;
    }

    public void showTables() {
        Set<Entry<String, FileMap>> pairSet = tables.entrySet();
        for (Entry<String, FileMap> oneTable: pairSet) {
            System.out.println(oneTable.getKey() + " "
                + oneTable.getValue().size());
        }
    }

    public void setCurrentTable(FileMap newCurrentTable) {
        currentTable = newCurrentTable;
    }

    public FileMap getCurrentTable() {
        return currentTable;
    }

    public boolean init() throws IOException {
        String[] listOfFiles = new File(dataBaseDirectory).list();
        for (String oneFile: listOfFiles) {
            Path oneTablePath = Paths.get(dataBaseDirectory, oneFile);
            Path oneTableSignaturePath = Paths.get(dataBaseDirectory, oneFile, "signature.tsv");
            if (Files.isDirectory(oneTablePath) & Files.exists(oneTableSignaturePath)) {
                try (Scanner input = new Scanner(oneTableSignaturePath)) {
                    String[] types;
                    if (input.hasNext()) {
                        types = input.nextLine().trim().split("\\s+");
                        List<Class<?>> newTypeList = TypesUtils.toTypeList(types);
                        if (newTypeList != null) {
                            tables.put(oneFile, new FileMap(oneTablePath.toString(), newTypeList, this));
                        }
                    }
                } catch (FileNotFoundException e) {
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }
        }
        boolean allRight = true;
        Set<Entry<String, FileMap>> pairSet = tables.entrySet();
        for (Entry<String, FileMap> oneFileMap: pairSet) {
            if  (!oneFileMap.getValue().init()) {
                allRight = false;
            }
        }
        return allRight;
    }
}
