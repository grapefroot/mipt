package ru.fizteh.fivt.students.sautin1.junit.filemap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.nio.file.Path;

/**
 * String table provider class.
 * Created by sautin1 on 10/12/14.
 */
public class StringTableProvider extends GeneralTableProvider<String, StringTable> implements TableProvider {

    public StringTableProvider(Path rootDir, boolean autoCommit, TableIOTools<String, StringTable> tableIOTools) {
        super(rootDir, autoCommit, tableIOTools);
    }

    @Override
    public StringTable establishTable(String name) {
        return new StringTable(name, autoCommit);
    }

}
