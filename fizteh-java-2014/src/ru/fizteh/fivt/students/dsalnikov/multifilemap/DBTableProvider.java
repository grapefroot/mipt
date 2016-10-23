package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.dsalnikov.utils.CorrectnessCheck;
import ru.fizteh.fivt.students.dsalnikov.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class DBTableProvider implements TableProvider {
    private String dbdir;

    public DBTableProvider(String dir) throws IOException {
        dbdir = dir;
        File f = new File(dir);
        if (dir == null || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("Incorrect provider invocation");
        }
        if (!f.exists() || !f.isDirectory()) {
            throw new IllegalArgumentException("Incorrect paths provided!!!!!1111111");
        }
    }

    @Override
    public Table getTable(String name) {
        File dir = new File(dbdir, name);
        return new MultiFileTable(dir.toString());
    }

    @Override
    public Table createTable(String name) {
        if (name == null || name.trim().isEmpty() || !CorrectnessCheck.isCorrectArgument(name)) {
            throw new IllegalArgumentException("Incorrect name");
        }
        File dir = new File(dbdir, name);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new MultiFileTable(dir.toString());
    }

    @Override
    public void removeTable(String name) {
        File f = new File(dbdir, name);
        try {
            FileUtils.forceRemoveDirectory(f);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
