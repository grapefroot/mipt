package ru.fizteh.fivt.students.dnovikov.multifilehashmap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class SingleTable {
    private Map<String, String> dataBase;
    private Path singleTablePath;
    private Table parentTable;
    private int folderNumber;
    private int fileNumber;

    public SingleTable(int folderNum, int fileNum, Table parent) throws LoadOrSaveException {
        dataBase = new TreeMap<>();
        parentTable = parent;
        folderNumber = folderNum;
        fileNumber = fileNum;
        singleTablePath = getPathToSaveOrLoad();
        this.load();
    }

    public String put(String key, String value) {
        return dataBase.put(key, value);
    }

    public String get(String key) {
        return dataBase.get(key);
    }

    public Set<String> list() {
        Set<String> keys = dataBase.keySet();
        return keys;
    }

    public String remove(String key) {
        return dataBase.remove(key);
    }

    public void save() throws LoadOrSaveException {
        if (singleTablePath.toFile().isDirectory()) {
            throw new LoadOrSaveException("cannot load table: '" + singleTablePath.getFileName() + "' is directory");
        } else {
            File pathToSave = getPathToSaveOrLoad().toFile();
            if (dataBase.size() == 0) {
                if (pathToSave.exists()) {
                    if (!pathToSave.delete()) {
                        throw new LoadOrSaveException("can't delete " + pathToSave.getAbsolutePath());
                    }
                }
                File parentDir = new File(pathToSave.getParent());
                String[] list = parentDir.list();
                if (parentDir.exists() && (list == null || list.length == 0)) {
                    if (!parentDir.delete()) {
                        throw new LoadOrSaveException("can't delete directory " + parentDir);
                    }
                }
            } else {

                if (!getFolder().toFile().exists()) {
                    try {
                        Files.createDirectory(getFolder());
                    } catch (IOException e) {
                        throw new LoadOrSaveException("can't create directory: " + getFolder().toAbsolutePath());
                    }
                }
                try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(singleTablePath))) {
                    Set<Entry<String, String>> list = dataBase.entrySet();
                    for (Entry<String, String> entry : list) {
                        writeString(outputStream, entry.getKey());
                        writeString(outputStream, entry.getValue());
                    }
                } catch (IOException e) {
                    throw new LoadOrSaveException("cannot write to database");
                }
            }
        }
    }

    public Path getFolder() {
        return parentTable.getTableDirectory().resolve(folderNumber + ".dir");
    }

    public Path getPathToSaveOrLoad() {
        return getFolder().resolve(fileNumber + ".dat");
    }

    public void load() throws LoadOrSaveException {
        if (singleTablePath.toFile().isDirectory()) {
            throw new LoadOrSaveException("cannot load table: '" + singleTablePath.toFile().toString()
                    + "' is directory");
        } else if (singleTablePath.toFile().exists()) {

            try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(singleTablePath))) {
                dataBase.clear();
                while (inputStream.available() > 0) {
                    dataBase.put(readString(inputStream), readString(inputStream));
                }
            } catch (IOException e) {
                try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(singleTablePath))) {
                    outputStream.flush();
                } catch (IOException exception) {
                    throw new LoadOrSaveException("cannot create file with database");
                } catch (SecurityException exception) {
                    throw new LoadOrSaveException("cannot create file with database");
                }
            }
        }
    }

    public String readString(DataInputStream inputStream) throws IOException {
        try {
            int length = inputStream.readInt();
            if (length <= 0) {
                throw new IOException("cannot read from database");
            }
            byte[] byteString = new byte[length];
            inputStream.readFully(byteString);
            return new String(byteString, "UTF-8");
        } catch (EOFException e) {
            throw new IOException("cannot read from database");
        } catch (IOException e) {
            throw new IOException("cannot read from database");
        }
    }

    public void writeString(DataOutputStream outputStream, String string) throws IOException {
        try {
            byte[] stringByte = string.getBytes("UTF-8");
            outputStream.writeInt(stringByte.length);
            outputStream.write(stringByte);
        } catch (IOException e) {
            throw new IOException("cannot write to database");
        }
    }

    public void drop() throws IOException, LoadOrSaveException {
        dataBase.clear();
        save();
    }

    public int size() {
        return dataBase.size();
    }
}
