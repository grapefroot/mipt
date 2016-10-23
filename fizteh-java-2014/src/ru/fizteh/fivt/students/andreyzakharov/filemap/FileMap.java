package ru.fizteh.fivt.students.andreyzakharov.filemap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FileMap extends HashMap<String, String> {
    Path dbPath;

    public FileMap(Path path) throws ConnectionInterruptException {
        dbPath = path;
        load();
    }

    public void load() throws ConnectionInterruptException {
        try (DataInputStream is = new DataInputStream(Files.newInputStream(dbPath))) {
            clear();

            while (is.available() > 0) {
                int keyLen = is.readInt();
                byte[] key = new byte[keyLen];
                int keyRead = is.read(key, 0, keyLen);
                if (keyRead != keyLen) {
                    throw new ConnectionInterruptException("database: db file is invalid");
                }
                int valLen = is.readInt();
                byte[] value = new byte[valLen];
                int valRead = is.read(value, 0, valLen);
                if (valRead != valLen) {
                    throw new ConnectionInterruptException("database: db file is invalid");
                }
                put(new String(key, "UTF-8"), new String(value, "UTF-8"));
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: reading from disk failed");
        }
    }

    public void unload() throws ConnectionInterruptException {
        try (DataOutputStream os = new DataOutputStream(Files.newOutputStream(dbPath))) {
            for (HashMap.Entry<String, String> entry : entrySet()) {
                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                os.writeInt(key.length);
                os.write(key);
                os.writeInt(value.length);
                os.write(value);
            }
        } catch (IOException e) {
            throw new ConnectionInterruptException("database: writing to disk failed");
        }
    }
}
