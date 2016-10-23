package ru.fizteh.fivt.students.akhtyamovpavel.databaselibrary;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.*;


public class DataBaseTableTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static DataBaseTableProviderFactory factory = new DataBaseTableProviderFactory();
    private static DataBaseTableProvider database;

    String folderName;

    @Before
    public void initDataBase() {
        folderName = folder.toString();
        database = factory.create(folderName);
    }

    @Test
    public void testGet() {
        database.createTable("table");
        Table table1 = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            table1.put(Integer.toString(i), Integer.toString(i));
        }
        for (int i = 0; i < 100; ++i) {
            assertEquals(Integer.toString(i), table1.get(Integer.toString(i)));
        }

        for (int i = 101; i < 200; ++i) {
            assertNull(table1.get(Integer.toString(i)));
        }
    }

    @Test
    public void testPut() {
        database.createTable("table");
        Table table2 = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            assertEquals(table2.put(Integer.toString(i), Integer.toString(i)), Integer.toString(i));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(table2.put(Integer.toString(i), Integer.toString(i + 200)), Integer.toString(i));
        }

        try {
            table2.put(null, "haha");
            assertFalse(true);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }

        try {
            table2.put("haha", null);
            assertFalse(true);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }

    @Test
    public void testList() {
        database.createTable("table");
        Table table = database.getTable("table");
        ArrayList<String> keyList = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), Integer.toString(i));
            keyList.add(Integer.toString(i));
        }
        ArrayList<String> tableList = new ArrayList<>(table.list());
        tableList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        keyList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        assertEquals(tableList, keyList);
    }

    @Test
    public void testRemove() {
        database.createTable("table");
        Table table = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), Integer.toString(i));
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(Integer.toString(i),
                    table.remove(Integer.toString(i)));
        }

        for (int i = 0; i < 100; ++i) {
            assertNull(table.remove(Integer.toString(i)));
        }

        try {
            table.remove(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }

    @Test
    public void testSize() {
        database.createTable("table");
        Table table = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), Integer.toString(i));
        }

        assertEquals(table.size(), 100);
        for (int i = 50; i < 1000; ++i) {
            table.put(Integer.toString(i), "hello");
        }
        assertEquals(table.size(), 1000);

        table.commit();

        for (int i = 0; i < 500; ++i) {
            table.remove(Integer.toString(i));
        }


        assertEquals(table.size(), 500);

        table.rollback();

        assertEquals(table.size(), 1000);
    }

    @Test
    public void testCommitRollback() {
        database.createTable("table");
        Table table = database.getTable("table");
        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), Integer.toString(i));
        }
        assertEquals(table.size(), 100);
        assertEquals(table.rollback(), 100);
        assertEquals(table.size(), 0);

        for (int i = 0; i < 100; ++i) {
            table.put(Integer.toString(i), Integer.toString(i));
        }
        assertEquals(table.commit(), 100);
        assertEquals(table.size(), 100);

        /*
            Test for correct writing to file
         */
        DataBaseTable loadedTable = null;
        try {
            loadedTable = new DataBaseTable(Paths.get(folderName), "table");
        } catch (Exception e) {
            assertTrue(false);
        }
        ArrayList<String> list = new ArrayList<>(loadedTable.list());
        for (String key : list) {
            assertEquals(loadedTable.get(key), key);
        }

    }

    @After
    public void release() {
        folder.delete();
    }


}
