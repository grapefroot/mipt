package ru.phystech.java2.utils;

import org.apache.log4j.Logger;
import ru.phystech.java2.structured.Storeable;
import ru.phystech.java2.structured.Table;
import ru.phystech.java2.structured.TableProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Map;

public class WorkWithDatFiles {
    private static Logger logger = Logger.getLogger(WorkWithDatFiles.class);

    public static void readIntoMap(File dataBaseFile, Map<String, String> map) throws IOException {
        RandomAccessFile dataBaseFileReader = new RandomAccessFile(dataBaseFile, "rw");
        long lenght = dataBaseFile.length();
        byte[] buffer;

        while (lenght > 0) {
            int keyLenght = dataBaseFileReader.readInt();
            lenght -= 4;
            int valueLenght = dataBaseFileReader.readInt();
            lenght -= 4;

            buffer = new byte[keyLenght];
            dataBaseFileReader.readFully(buffer);
            lenght -= buffer.length;
            String key = new String(buffer, StandardCharsets.UTF_8);

            buffer = new byte[valueLenght];
            dataBaseFileReader.readFully(buffer);
            lenght -= buffer.length;
            String value = new String(buffer, StandardCharsets.UTF_8);

            map.put(key, value);
        }
        dataBaseFileReader.close();
    }

    private static boolean checkKeyPlacement(int indexDir, int indexDat, String key) {
        int hashCode = key.hashCode();
        hashCode *= Integer.signum(hashCode);
        int dir = hashCode % 16;
        int dat = hashCode / 16 % 16;
        return (dir == indexDir && dat == indexDat);
    }

    public static void readIntoStoreableMap(File dataBaseFile, Map<String, Storeable> map, Table table,
                                            TableProvider provider, int indexDir, int indexDat)
            throws IOException, ParseException {
        RandomAccessFile dataBaseFileReader = new RandomAccessFile(dataBaseFile, "rw");
        long length = dataBaseFile.length();
        byte[] buffer;

        while (length > 0) {
            int keyLength = dataBaseFileReader.readInt();
            length -= 4;
            int valueLength = dataBaseFileReader.readInt();
            length -= 4;

            buffer = new byte[keyLength];
            dataBaseFileReader.readFully(buffer);
            length -= buffer.length;
            String key = new String(buffer, StandardCharsets.UTF_8);

            if (!checkKeyPlacement(indexDir, indexDat, key)) {
                throw new IOException("wrong key placement");
            }

            buffer = new byte[valueLength];
            dataBaseFileReader.readFully(buffer);
            length -= buffer.length;
            String value = new String(buffer, StandardCharsets.UTF_8);

            map.put(key, provider.deserialize(table, value));
        }
        dataBaseFileReader.close();
    }

    public static void writeIntoFile(File dataBaseFile, Map<String, String> map) throws IOException {
        RandomAccessFile dataBaseFileWriter = new RandomAccessFile(dataBaseFile, "rw");
        try {
            dataBaseFileWriter.setLength(0);
            for (Map.Entry<String, String> element : map.entrySet()) {
                String key = element.getKey();
                byte[] bufferKey = key.getBytes(StandardCharsets.UTF_8);
                dataBaseFileWriter.writeInt(bufferKey.length);

                String value = element.getValue();
                byte[] bufferValue = value.getBytes(StandardCharsets.UTF_8);
                dataBaseFileWriter.writeInt(bufferValue.length);

                dataBaseFileWriter.write(bufferKey);
                dataBaseFileWriter.write(bufferValue);
            }
        } catch (IOException exc) {
            System.err.println(exc.getMessage());
            logger.error(exc.getMessage(), exc);
        } finally {
            dataBaseFileWriter.close();
        }
    }
}
