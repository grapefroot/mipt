package ru.fizteh.fivt.students.Volodin_Denis.FileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataBase {

    private String databasePath;
    private Map<String, String> database;

    public DataBase(final String dbPath) throws Exception {
        databasePath = dbPath;
        database = new HashMap<String, String>();
        if (Paths.get(databasePath).normalize().toFile().exists()) {
            readFromDisk();
        } else {
            File file = new File(databasePath);
            file.createNewFile();
        }
    }

    public String[] list() throws Exception {
        try {
            if (database.isEmpty()) {
                return new String[0];
            }
            Set<String> keysList = database.keySet();
            String[] listKeys = new String[database.size()];
            int i = 0;
            for (String key : keysList) {
                listKeys[i] = key;
                ++i;
            }
            return listKeys;
        } catch (Exception e) {
            filemapSmthWrong("list", e.getMessage());
        }
        return new String[0]; // Unreachable code, add return to ignore Eclipse warning.
    }

    public void put(final String key, final String value) throws Exception {
        try {
            database.put(key, value);
        } catch (Exception e) {
            filemapSmthWrong("put", e.getMessage());
        }
    }

    public String search(final String key) throws Exception {
        try {
            return database.get(key);
        } catch (Exception e) {
            filemapSmthWrong("search", e.getMessage());
        }
        return database.get(key); // Unreachable code, add return to ignore Eclipse warning.
    }

    public void remove(final String key) throws Exception {
        try {
            database.remove(key);
        } catch (Exception e) {
            filemapSmthWrong("remove", e.getMessage());
        }
    }

    public void readFromDisk() throws Exception {
        String key;
        String value;
        FileInputStream input = new FileInputStream(databasePath);
        FileChannel channel = input.getChannel();
        ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        try {
            while (buffer.hasRemaining()) {
                byte[] word = new byte[buffer.getInt()];
                buffer.get(word, 0, word.length);
                key = new String(word, "UTF-8");
                
                word = new byte[buffer.getInt()];
                buffer.get(word, 0, word.length);
                value = new String(word, "UTF-8");
                
                database.put(key, value);
            }
            input.close();
        } catch (Exception e) {
            input.close();
            filemapErrorRead("read");
        }
    }

    public void writeOnDisk() throws Exception {
        FileOutputStream output = new FileOutputStream(databasePath);
        Set<String> keyList = database.keySet();
        try {
            for (String key : keyList) {
                ByteBuffer buffer1 = ByteBuffer.allocate(4);
                byte[] keyByte = buffer1.putInt(key.getBytes("UTF-8").length).array();
                output.write(keyByte);
                output.write(key.getBytes("UTF-8"));
                
                
                ByteBuffer buffer2 = ByteBuffer.allocate(4);
                byte[] valueByte = buffer2.putInt(database.get(key).getBytes("UTF-8").length).array();
                output.write(valueByte);
                output.write(database.get(key).getBytes("UTF-8"));
            }
            output.close();
        } catch (Exception e) {
            output.close();
            filemapErrorWrite("write");
        }
    }

    private void filemapErrorRead(final String commandName) throws Exception {
        throw new Exception(commandName + " : error reading from file");
    }
    
    private void filemapErrorWrite(final String commandName) throws Exception {
        throw new Exception(commandName + " : error writing to file");
    }
    
    private void filemapSmthWrong(final String commandName, final String message) throws Exception {
        throw new Exception(commandName + " :" + message);
    }
}
