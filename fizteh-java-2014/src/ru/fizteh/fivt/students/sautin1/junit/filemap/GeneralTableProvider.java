package ru.fizteh.fivt.students.sautin1.junit.filemap;

import ru.fizteh.fivt.students.sautin1.junit.shell.FileUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table provider class.
 * Created by sautin1 on 10/12/14.
 */
public abstract class GeneralTableProvider<MappedValue, T extends GeneralTable<MappedValue>> {
    private Map<String, T> tableMap;
    private final Path rootDir;
    protected final boolean autoCommit;
    private final TableIOTools<MappedValue, T> tableIOTools;

    public GeneralTableProvider(Path rootDir, boolean autoCommit, TableIOTools<MappedValue, T> tableIOTools) {
        if (rootDir == null) {
            throw new IllegalArgumentException("No directory provided");
        }
        if (!Files.exists(rootDir) || !Files.isDirectory(rootDir)) {
            try {
                Files.createDirectory(rootDir);
            } catch (IOException e) {
                String errorMessage = "Directory " + rootDir.getFileName().toString();
                errorMessage += " does not exist and cannot be created";
                throw new IllegalArgumentException(errorMessage);
            }
            //throw new IllegalArgumentException("Directory " + rootDir + " does not exist");
        }
        if (tableIOTools == null) {
            throw new IllegalArgumentException("Wrong TableIOTools instance");
        }
        this.rootDir = rootDir;
        tableMap = new HashMap<>();
        this.autoCommit = autoCommit;
        this.tableIOTools = tableIOTools;
    }

    protected GeneralTableProvider(Path rootDir, TableIOTools<MappedValue, T> tableIOTools) {
        this(rootDir, true, tableIOTools);
    }

    /**
     * Getter of rootDir field.
     * @return rootDir field.
     */
    public Path getRootDir() {
        return rootDir;
    }

    /**
     * Create a new table instance.
     * @param name - name of the new table.
     * @return new table instance.
     */
    public abstract T establishTable(String name);

    /**
     * Getter of the table with given name.
     * @param name - name of the table.
     * @return table with corresponding name.
     */
    public T getTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("No name provided");
        }
        return tableMap.get(name);
    }

    /**
     * Create new table with given name.
     * @param name - name of the new table.
     * @return new table.
     */
    public T createTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("No name provided");
        }
        Path tablePath = rootDir.resolve(name);
        if (tableMap.get(name) != null) {
            return null;
        }
        try {
            Files.createDirectory(tablePath);
        } catch (FileAlreadyExistsException e) {
            throw new IllegalArgumentException("Directory with such name already exists");
        } catch (IOException e) {
            throw new IllegalArgumentException("IO error");
        }

        T newTable = establishTable(name);
        tableMap.put(name, newTable);

        return newTable;
    }

    /**
     * Remove table by its name. All table files are deleted.
     * @param name - name of the table.
     */
    public void removeTable(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("No name provided");
        }
        T oldTable = tableMap.remove(name);
        if (oldTable == null) {
            throw new IllegalStateException("Table " + name + " doesn't exist");
        }
        try {
            FileUtils.removeDirectory(rootDir.resolve(name));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Commits table and saves it to file.
     * @param tableName - name of the table to commit.
     * @return diff count.
     * @throws IOException if any IO error occurs.
     */
    public int commitTable(String tableName) throws IOException {
        T table = tableMap.get(tableName);
        int diffCount = table.commit();
        saveTable(rootDir, tableName);
        return diffCount;
    }

    /**
     * Load table from the file.
     * @param root - path to the root directory.
     * @param tableName - name of the table to load.
     * @throws java.io.IOException if any IO error occured.
     */
    public void loadTable(Path root, String tableName) throws IOException {
        T table = tableIOTools.readTable(root, this, tableName);
        table.commit();
        tableMap.put(table.getName(), table);
    }

    /**
     * Save table to file.
     * @param root - path to the root directory.
     * @param tableName - name of the table to save.
     */
    public void saveTable(Path root, String tableName) throws IOException {
        T table = tableMap.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table " + tableName + " doesn't exist");
        }
        tableIOTools.writeTable(root, table);
    }

    /**
     * Save all tables to provider.
     * @throws java.io.IOException if any IO error occurs.
     */
    public void saveAllTables() throws IOException {
        for (String key : tableMap.keySet()) {
            saveTable(rootDir, key);
        }
    }

    /**
     * Load all tables to provider.
     * @throws java.io.IOException if any IO error occurs.
     */
    public void loadAllTables() throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootDir)) {
            for (Path entry : directoryStream) {
                Path filePath = entry.getFileName();
                loadTable(rootDir, filePath.toString());
            }
        }
    }

    /**
     * List all tables in the database.
     * @return list of tables in the database.
     */
    public List<T> listTables() {
        return new ArrayList<>(tableMap.values());
    }
}
