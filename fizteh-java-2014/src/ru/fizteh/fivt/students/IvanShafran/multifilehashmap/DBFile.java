package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;


import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell.CommandRM;

import java.io.*;
import java.util.HashMap;

public class DBFile {
    private File workingFile;
    private HashMap<String, String> hashMap;

    public File getWorkingFile() throws Exception {
        return workingFile;
    }

    public void setWorkingFile(File file) {
        workingFile = file;
    }

    public HashMap<String, String> getHashMap() {
            return hashMap;
    }

    DBFile(File file) {
        setWorkingFile(file);
        hashMap = new HashMap<>();
    }

    private String readString(DataInputStream inputStream) throws Exception {
        try {
            int length = inputStream.readInt();
            if (length <= 0) {
                throw new Exception("read from database failed");
            }
            if (length > (1 << 20)) {
                throw new Exception("read from database failed: string too big");
            }
            byte[] byteString = new byte[length];
            inputStream.readFully(byteString);
            return new String(byteString, "UTF-8");
        } catch (Exception e) {
            throw new Exception("read from database failed");
        }
    }

    public void readFile() throws Exception {
        if (!workingFile.exists()) {
            hashMap = new HashMap<>();
            return;
        }

        HashMap readingHashMap = new HashMap();

        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(workingFile))) {

            while (dataInputStream.available() != 0) {
                String key = readString(dataInputStream);
                String value = readString(dataInputStream);

                readingHashMap.put(key, value);
            }
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        hashMap = readingHashMap;
    }

    public int getNumberOfItems() {
        return hashMap.size();
    }

    private void writeString(DataOutputStream outputStream, String string) throws Exception {
        try {
            byte[] stringByte = string.getBytes("UTF-8");
            outputStream.writeInt(stringByte.length);
            outputStream.write(stringByte);
        } catch (Exception e) {
            throw new Exception("writing to database failed");
        }
    }

    public void writeHashMapToFile() throws Exception {
        if (!workingFile.getParentFile().exists()) {
            File parent = workingFile.getParentFile();
            try {
                parent.mkdir();
            } catch (Exception e) {
                throw new Exception(parent.toString() + "didn't create");
            }
        }

        if (!workingFile.exists()) {
            try {
                workingFile.createNewFile();
            } catch (Exception e) {
                throw new Exception(workingFile.toString() + "didn't create");
            }
        }

        try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(workingFile))) {
            for (String key : hashMap.keySet()) {
                writeString(dataOutputStream, key);
                writeString(dataOutputStream, hashMap.get(key));
            }
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        if (hashMap.size() == 0) {
            CommandRM remove = new CommandRM();
            remove.execute(workingFile.getAbsoluteFile().toString());
        }
    }

}
