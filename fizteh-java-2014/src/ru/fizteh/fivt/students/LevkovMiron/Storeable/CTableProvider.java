package ru.fizteh.fivt.students.LevkovMiron.Storeable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CTableProvider implements TableProvider {
    private Path rootDir;
    private CTable currentTable;
    private Map<String, CTable> listTables;
    private Parser parser = new Parser();

    CTableProvider(Path dir) throws IOException {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }
        } catch (IOException e) {
            throw new IOException("Path doesn't exist, can't be created");
        }
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Destination is not a directory");
        }
        rootDir = dir;
        listTables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        CTable table = new CTable(file);
                        listTables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                throw new IOException("Can't load the database: " + e.getMessage());
            }
    }

    public Set<String> showTables() {
        for (String s : listTables.keySet()) {
            System.out.println(s + " " + listTables.get(s).getKeysCount());
        }
        return listTables.keySet();
    }

    @Override
    public void removeTable(String name) throws IOException, IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("Illegal argument: null");
        }
        CTable table = listTables.get(name);
        if (table != null) {
            if (currentTable == table) {
                currentTable = null;
            }
            listTables.remove(name);
            table.drop();
            System.out.println("dropped");
        } else {
            throw new IllegalStateException("Table doesn't exist");
        }
    }
    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Illegal argument: null");
        }
        return listTables.get(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if (name == null || columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Illegal argument: null");
        }
        Path path = rootDir.resolve(name);
        if (!listTables.containsKey(name)) {
            if (Files.exists(path)) {
                if (!Files.isDirectory(path)) {
                    throw new IOException("Destination isn't a directory");
                }
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    if (stream.iterator().hasNext()) {
                        throw new IOException("Destination isn't empty");
                    }
                }
            } else {
                Files.createDirectory(path);
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    if (stream.iterator().hasNext()) {
                        throw new IOException("Destination isn't empty");
                    }
                }
            }
            listTables.put(name, new CTable(path, columnTypes));
            return listTables.get(name);
        }
        return null;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return parser.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return parser.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        return new CStoreable(values);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException {
        List<Object> listObjects = new ArrayList<>(values);
        if (listObjects.size() != table.getColumnsCount()) {
            throw new ColumnFormatException("Wrong number of columns");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (listObjects.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("Illegal column type: column " + i
                                                + " should be " + table.getColumnType(1));
            }
        }
        return new CStoreable(listObjects);
    }
}
