package ru.phystech.java2.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorkWithDirs {

    private static void makeUpParsedMap(Map<String, String>[][] mapReadyForWrite, Map<String, String> dataBase) {
        for (String key : dataBase.keySet()) {
            int hashCode = key.hashCode();
            hashCode *= Integer.signum(hashCode);
            int indexDir = hashCode % 16;
            int indexDat = hashCode / 16 % 16;

            if (mapReadyForWrite[indexDir][indexDat] == null) {
                mapReadyForWrite[indexDir][indexDat] = new HashMap<>();
            }
            mapReadyForWrite[indexDir][indexDat].put(key, dataBase.get(key));
        }
    }

    private static void cleanEmpryDirs(File fileDir) {
        if (fileDir.exists()) {
            if (fileDir.listFiles().length == 0) {
                try {
                    FileUtils.deleteDirectory(fileDir);
                } catch (Exception exc) {
                    System.err.println(exc.getMessage());
                }
            }
        }
    }

    public static void writeIntoFiles(File dataBaseDirectory, Map<String, String> dataBase) throws IOException {
        Map<String, String>[][] mapReadyForWrite = new Map[16][16];
        makeUpParsedMap(mapReadyForWrite, dataBase);

        for (int indexDir = 0; indexDir < 16; ++indexDir) {
            File fileDir = new File(dataBaseDirectory, indexDir + ".dir");
            for (int indexDat = 0; indexDat < 16; ++indexDat) {
                File fileDat = new File(fileDir, indexDat + ".dat");
                if (mapReadyForWrite[indexDir][indexDat] == null) {
                    if (fileDat.exists()) {
                        if (!fileDat.delete()) {
                            throw new IOException(fileDat.toPath().toString() + ": can not delete file");
                        }
                    }
                    continue;
                }

                if (!fileDir.exists()) {
                    if (!fileDir.mkdir()) {
                        throw new IOException(fileDat.toString() + ": can not create file, something went wrong");
                    }
                }

                if (!fileDat.createNewFile()) {
                    if (!fileDat.exists()) {
                        throw new IOException(fileDat.toString() + ": can not create file, something went wrong");
                    }
                }
                WorkWithDatFiles.writeIntoFile(fileDat, mapReadyForWrite[indexDir][indexDat]);
            }
            cleanEmpryDirs(fileDir);
        }
    }

    public static void readIntoDataBase(File dataBaseDirectory, Map<String, String> map) throws IOException {
        if (!dataBaseDirectory.exists() || dataBaseDirectory.isFile()) {
            throw new IOException(dataBaseDirectory + ": not directory or not exist");
        }

        for (int index = 0; index < 16; ++index) {
            String indexDirectoryName = index + ".dir";
            File indexDirectory = new File(dataBaseDirectory, indexDirectoryName);

            if (!indexDirectory.exists()) {
                continue;
            }

            if (!indexDirectory.isDirectory()) {
                throw new IOException(indexDirectory.toString() + ": not directory");
            }

            for (int fileIndex = 0; fileIndex < 16; ++fileIndex) {
                String fileIndexName = fileIndex + ".dat";
                File fileIndexDat = new File(indexDirectory, fileIndexName);

                if (!fileIndexDat.exists()) {
                    continue;
                }
                WorkWithDatFiles.readIntoMap(fileIndexDat, map);
            }
        }
    }
}
