 package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;


/**
 * Created by Aliaksei Semchankau on 09.11.2014.
 */
public class DatabaseFactory implements TableProviderFactory{

    @Override
    public DatabaseProvider create(String dir) {

        if (dir == null) {
            throw new IllegalArgumentException("incorrect direction for DatabaseFactory");
        }

        DatabaseProvider dProvider = new DatabaseProvider(dir);
        return dProvider;
    }
}
