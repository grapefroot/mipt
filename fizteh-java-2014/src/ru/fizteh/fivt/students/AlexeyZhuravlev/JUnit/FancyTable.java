package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.MultiTable;

/**
 * @author AlexeyZhuravlev
 */
public class FancyTable extends MultiTable {

    public FancyTable() {
        databases = new FancyDataBase[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                databases[i][j] = new FancyDataBase();
            }
        }
    }

    public void importMap(MultiTable other) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (other.databases[i][j] == null) {
                    databases[i][j] = new FancyDataBase();
                } else {
                    databases[i][j] = new FancyDataBase(other.databases[i][j].data);
                }
            }
        }
    }
}
