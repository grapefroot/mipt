package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MFileHashMapFactory implements TableProviderFactory {
    public MFileHashMapFactory() {}

    public TableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException();
        }
        Path dataBaseDirectory
                = Paths.get(System.getProperty("user.dir")).resolve(dir);

        if (Files.exists(dataBaseDirectory)) {
            if (!Files.isDirectory(dataBaseDirectory)) {
                throw new IllegalArgumentException();
            }
        } else {
            try {
                Files.createDirectory(dataBaseDirectory);
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }

        try {
            MFileHashMap myMFileHashMap = new MFileHashMap(dataBaseDirectory.toString());
            return myMFileHashMap;
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
