package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProviderFactory;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.gen.TableRowGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DataBaseTableProviderTest {

    private static DataBaseTableProvider database;
    private static DataBaseTableProviderFactory factory;
    static TemporaryFolder folder;
    static String folderPath;

    @BeforeClass
    public static void initDatabase() {
        factory = new DataBaseTableProviderFactory();
        try {
            folder = new TemporaryFolder();
            folder.create();
            folderPath = folder.getRoot().getAbsolutePath();
            database = factory.create(folderPath);
        } catch (IOException ioe) {
            assertTrue(false);
        }
    }


    @Test
    public void testCreate() {

        try {
            Table table = database.createTable(null, TableRowGenerator.generateSignature());
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertFalse(false);
        } catch (IOException ioe) {
            assertTrue(false);
        }

        try {
            Table table = database.createTable("table", TableRowGenerator.generateSignature());
            assertNotNull(table);
        } catch (IOException ioe) {
            assertTrue(false);
        }

        try {
            Table table2 = database.createTable("table", TableRowGenerator.generateSignature());
            assertNull(table2);
        } catch (IOException ioe) {
            assertTrue(false);
        }
        database.removeTable("table");
    }

    @Test
    public void testDrop() {
        try {
            database.removeTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertTrue(true);
        } catch (IllegalStateException ise) {
            assertTrue(false);
        }

        try {
            for (int i = 0; i < 50; ++i) {
                database.createTable(Integer.toString(i), TableRowGenerator.generateSignature());
            }
        } catch (IOException ioe) {
            assertTrue(false);
        }

        for (int i = 51; i < 100; ++i) {
            try {
                database.removeTable(Integer.toString(i));
                assertTrue(false);
            } catch (IllegalStateException ise) {
                assertTrue(true);
            }
        }

        for (int i = 0; i < 25; ++i) {
            try {
                database.removeTable(Integer.toString(i));
            } catch (Exception e) {
                assertTrue(false);
            }
        }
        try {
            for (int i = 0; i < 25; ++i) {
                Table table = database.createTable(Integer.toString(i), TableRowGenerator.generateSignature());
                assertNotNull(table);
            }
        } catch (IOException ioe) {
            assertTrue(false);
        }

        //Release tables

        for (int i = 0; i < 50; ++i) {
            database.removeTable(Integer.toString(i));
        }
    }

    @Test
    public void testCreatingDatabase() {
        try {
            Files.createFile(Paths.get(folderPath, "lol.dir"));
        } catch (IOException ioe) {
            assertTrue(false);
        }
        try {
            DataBaseTableProvider database1 = factory.create(Paths.get(folderPath, "lol.dir").toString(), true);
            assertNull(database1);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            Files.delete(Paths.get(folderPath, "lol.dir"));
        } catch (IOException ioe) {
            assertTrue(false);
        }
    }


    @Test
    public void testShowTables() {
        Table notExistsTable = database.getTable("notExists");
        assertNull(notExistsTable);

        try {
            database.getTable(null);
            assertTrue(false);
        } catch (IllegalArgumentException iae) {
            assertFalse(false);
        }

        HashMap<String, ArrayList<Class<?>>> signatures = new HashMap<>();
        try {
            for (int i = 0; i < 100; ++i) {
                signatures.put(Integer.toString(i), TableRowGenerator.generateSignature());
                Table table = database.createTable(Integer.toString(i), signatures.get(Integer.toString(i)));
            }
        } catch (IOException ioe) {
            assertTrue(false);
        }

        try {
            for (int i = 0; i < 100; ++i) {
                Table table = database.getTable(Integer.toString(i));
                for (int j = 0; j < 50; ++j) {
                    table.put(Integer.toString(j),
                            TableRowGenerator.generateRow(database, (DataBaseTable) table,
                                    signatures.get(Integer.toString(i))));
                }
                try {
                    if (i < 99) {
                        table = database.getTable(Integer.toString(i + 1));
                        assertTrue(false);
                    }
                } catch (IllegalArgumentException iae) {
                    assertTrue(true);
                }
                table.commit();
                try {
                    if (i < 99) {
                        table = database.getTable(Integer.toString(i + 1));
                    }
                } catch (IllegalArgumentException iae) {
                    assertTrue(false);
                }
            }
        } catch (IOException ioe) {
            assertTrue(false);
        }

        for (int i = 0; i < 100; ++i) {
            database.removeTable(Integer.toString(i));
        }

    }


    @Test
    public void openDatabase() {
        for (int i = 0; i < 100; ++i) {
            ArrayList<Class<?>> signature = TableRowGenerator.generateSignature();
            try {
                database.createTable(Integer.toString(i), signature);
            } catch (IOException e) {
                assertTrue(false);
            }
            Table table = database.getTable(Integer.toString(i));
            for (int j = 0; j < 50; ++j) {
                table.put(Integer.toString(i),
                        TableRowGenerator.generateRow(database, (DataBaseTable) table, signature));
            }
            try {
                table.commit();
            } catch (IOException ioe) {
                assertTrue(false);
            }
        }

        try {
            DataBaseTableProvider normalDatabase = factory.create(folderPath, true);
        } catch (Exception ioe) {
            assertTrue(false);
        }

        try {
            Files.createFile(Paths.get(folderPath, "lol.dir"));
            DataBaseTableProvider brokenDatabase = new DataBaseTableProvider(folderPath, true);
        } catch (IOException ioe) {
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        } finally {
            try {
                Files.delete(Paths.get(folderPath, "lol.dir"));
            } catch (IOException ioe) {
                assertTrue(false);
            }
        }

        try {
            TemporaryFolder databaseFolder = new TemporaryFolder();
            String dataBaseFolderName = databaseFolder.toString();
            Files.createDirectory(Paths.get(dataBaseFolderName));
            Files.createDirectory(Paths.get(dataBaseFolderName, "1"));
            Files.createDirectory(Paths.get(dataBaseFolderName, "1", "16.dir"));
            DataBaseTableProvider brokenDatabase = new DataBaseTableProvider(dataBaseFolderName, true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        for (int i = 0; i < 100; ++i) {
            database.removeTable(Integer.toString(i));
        }

    }
}
