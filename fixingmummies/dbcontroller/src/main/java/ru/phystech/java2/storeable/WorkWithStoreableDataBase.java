package ru.phystech.java2.storeable;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import ru.phystech.java2.structured.Storeable;
import ru.phystech.java2.structured.Table;
import ru.phystech.java2.structured.TableProvider;
import ru.phystech.java2.utils.StoreableCmdParseAndExecute;
import ru.phystech.java2.utils.WorkWithDatFiles;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class WorkWithStoreableDataBase {

    private static Logger log = Logger.getLogger(WorkWithStoreableDataBase.class);

    private static void makeUpParsedMap(Map<String, String>[][] mapReadyForWrite, Map<String, Storeable> dataBase,
                                        Table table, TableProvider provider) {
        for (String key : dataBase.keySet()) {
            int hashCode = key.hashCode();
            hashCode *= Integer.signum(hashCode);
            int indexDir = hashCode % 16;
            int indexDat = hashCode / 16 % 16;
            if (mapReadyForWrite[indexDir][indexDat] == null) {
                mapReadyForWrite[indexDir][indexDat] = new HashMap<>();
            }
            mapReadyForWrite[indexDir][indexDat].put(key, provider.serialize(table, dataBase.get(key)));
        }
    }

    private static void cleanEmptyDirs(File fileDir) {
        if (fileDir.exists()) {
            if (fileDir.listFiles().length == 0) {
                try {
                    FileUtils.deleteDirectory(fileDir);
                } catch (Exception exc) {
                    System.err.println(exc.getMessage());
                    log.error(exc.getMessage(), exc);
                }
            }
        }
    }

    public static void writeIntoFiles(File dataBaseDirectory, Map<String, Storeable> dataBase,
                                      Table table, TableProvider provider) throws IOException {
        Map<String, String>[][] mapReadyForWrite = new Map[16][16];
        makeUpParsedMap(mapReadyForWrite, dataBase, table, provider);
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
            cleanEmptyDirs(fileDir);
        }
    }

    private static List<Class<?>> getListOfTypes(File dataBaseDirectory) throws ParseException {
        final File signature = new File(dataBaseDirectory, "signature.tsv");
        try {
            Scanner scanner = new Scanner(new FileInputStream(signature));
            if (!scanner.hasNextLine()) {
                throw new IOException("signature.tsv file is empty");
            }
            String[] types = scanner.nextLine().split("\\s");
            List<Class<?>> result = new ArrayList<>();
            for (Integer i = 0; i < types.length; ++i) {
                switch (types[i]) {
                    case "int":
                        result.add(Integer.class);
                        break;
                    case "long":
                        result.add(Long.class);
                        break;
                    case "byte":
                        result.add(Byte.class);
                        break;
                    case "float":
                        result.add(Float.class);
                        break;
                    case "double":
                        result.add(Double.class);
                        break;
                    case "boolean":
                        result.add(Boolean.class);
                        break;
                    case "String":
                        result.add(String.class);
                        break;
                    default:
                        throw new IOException("signature.tsv has a bad symbols");
                }
            }
            scanner.close();
            return result;
        } catch (IOException exc) {
            throw new ParseException("read signature failed", 11);
        }
    }

    public static void readIntoDataBase(final File dataBaseDirectory, Map<String, Storeable> map, StoreableTable table,
                                        TableProvider provider) throws IOException, ParseException {
        if (!dataBaseDirectory.exists() || dataBaseDirectory.isFile()) {
            throw new IOException(dataBaseDirectory + ": not directory or not exist");
        }

        List<Class<?>> types = getListOfTypes(dataBaseDirectory);
        table.setColumnTypes(types);

        for (int index = 0; index < 16; ++index) {
            String indexDirectoryName = index + ".dir";
            File indexDirectory = new File(dataBaseDirectory, indexDirectoryName);
            if (!indexDirectory.exists()) {
                continue;
            }
            if (!indexDirectory.isDirectory()) {
                throw new IOException(indexDirectory.toString() + ": not directory");
            }
            if (indexDirectory.list().length == 0) {
                throw new IOException(indexDirectory.toString() + ": is empty");
            }
            for (int fileIndex = 0; fileIndex < 16; ++fileIndex) {
                String fileIndexName = fileIndex + ".dat";
                File fileIndexDat = new File(indexDirectory, fileIndexName);
                if (!fileIndexDat.exists()) {
                    continue;
                }
                if (fileIndexDat.length() == 0) {
                    throw new IOException(fileIndexDat.toString() + ": is empty");
                }
                WorkWithDatFiles.readIntoStoreableMap(fileIndexDat, map, table, provider, index, fileIndex);
            }
        }

    }

    public static void createSignatureFile(File tableStorageDirectory, Table table) {
        try {
            File signatureFile = new File(tableStorageDirectory, "signature.tsv");
            if (!signatureFile.exists()) {
                signatureFile.createNewFile();
            }
            BufferedWriter signatureWriter = new BufferedWriter(new FileWriter(signatureFile));
            Integer countOfColumns = table.getColumnsCount();
            for (Integer i = 0; i < countOfColumns; ++i) {
                Class<?> type = table.getColumnType(i);
                switch (type.getCanonicalName()) {
                    case "java.lang.Integer":
                        signatureWriter.write("int");
                        break;
                    case "java.lang.Long":
                        signatureWriter.write("long");
                        break;
                    case "java.lang.Byte":
                        signatureWriter.write("byte");
                        break;
                    case "java.lang.Float":
                        signatureWriter.write("float");
                        break;
                    case "java.lang.Double":
                        signatureWriter.write("double");
                        break;
                    case "java.lang.Boolean":
                        signatureWriter.write("boolean");
                        break;
                    case "java.lang.String":
                        signatureWriter.write("String");
                        break;
                    default:
                        throw new IOException("signature.tsv creation: something went wrong");
                }
                signatureWriter.write(" ");
            }
            signatureWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<?>> createListOfTypes(List<String> args) throws IOException {
        return createListOfTypesFromString(args.get(2));
    }

    public static List<Class<?>> createListOfTypesFromString(String stringTypes) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        List<String> types = StoreableCmdParseAndExecute.splitByDelimiter(stringTypes, " ");
        for (String type : types) {
            switch (type) {
                case "int":
                    result.add(Integer.class);
                    break;
                case "long":
                    result.add(Long.class);
                    break;
                case "byte":
                    result.add(Byte.class);
                    break;
                case "float":
                    result.add(Float.class);
                    break;
                case "double":
                    result.add(Double.class);
                    break;
                case "boolean":
                    result.add(Boolean.class);
                    break;
                case "String":
                    result.add(String.class);
                    break;
                default:
                    throw new IOException("wrong type (input args has a bad symbols in types specification)");
            }
        }
        return result;
    }
}
