package ru.fizteh.fivt.students.gudkov394.Proxy.test;

/**
 * Created by kagudkov on 01.12.14.
 */


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.gudkov394.Proxy.TableProviderFactoryWithCloseAndToString;
import ru.fizteh.fivt.students.gudkov394.Proxy.TableProviderWithToStringAndClose;
import ru.fizteh.fivt.students.gudkov394.Proxy.TableWithCloseAndToString;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestTable {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public Table table;
    public TableProvider provider;
    public String dbDirPath;
    List<Class<?>> types;

    @Before
    public void initTable() throws IOException {
        tmpFolder.create();
        TableProviderFactory factory = new TableProviderFactoryWithCloseAndToString();
        dbDirPath = tmpFolder.newFolder("test").getAbsolutePath();
        provider = factory.create(dbDirPath);
        Class<?>[] arrayTypes = {Integer.class, Long.class, Float.class, Double.class,
                Boolean.class, String.class, Byte.class};
        types = Arrays.asList(arrayTypes);
        table = provider.createTable("table", types);
    }

    @After
    public void closeTableAndProvider() throws Exception {
        try {
            ((TableProviderWithToStringAndClose) provider).close();
            ((TableWithCloseAndToString) table).close();
        } catch (IllegalStateException e) {
            e.getMessage();
        }
        tmpFolder.delete();
    }

    @Test
    public void testGetName() {
        assertEquals("table", table.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNull() {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        table.remove(null);
    }

    @Test
    public void testPutAndGet() {
        Object[] values = {5, 6L, 5.2f, 5.4, true, null, (Byte) (byte) 3};
        Storeable value = provider.createFor(table, Arrays.asList(values));
        assertNull(table.put("key", value));
        Storeable ret = table.get("key");
        assertEquals(ret.getByteAt(6), (Byte) (byte) 3);
        assertEquals(ret.getStringAt(5), null);
        assertEquals(ret.getColumnAt(1), 6L);
        value.setColumnAt(0, 2);
        Storeable old = table.put("key", value);
        assertEquals(old.getColumnAt(0), 5);
        assertEquals(table.get("key").getColumnAt(0), 2);
        assertNull(table.get("nothere"));
    }

    @Test
    public void testPutAndRemove() {
        Object[] values = {5, 6L, 5.2f, 5.4, true, null, (Byte) (byte) 3};
        Storeable value = provider.createFor(table, Arrays.asList(values));
        assertNull(table.put("key", value));
        Storeable old = table.remove("key");
        assertEquals(old.getColumnAt(0), 5);
        assertEquals(old.getColumnAt(1), 6L);
        assertEquals(old.getColumnAt(6), (byte) 3);
        assertNull(table.remove("key"));
    }

    @Test(expected = ColumnFormatException.class)
    public void testIncorrectPut() throws IOException {
        types.set(1, Boolean.class);
        Object[] values = {false, 6L, 5.2f, 5.4, true, null, (Byte) (byte) 3};
        Table anotherTable = provider.createTable("anotherTable", types);
        Storeable value = provider.createFor(anotherTable, Arrays.asList(values));
        table.put("key", value);
    }

    @Test
    public void testSize() {
        Storeable value = provider.createFor(table);
        assertEquals(0, table.size());
        table.put("1", value);
        assertEquals(1, table.size());
        table.put("2", value);
        table.put("3", value);
        // table.put("2", value);
        assertEquals(3, table.size());
    }

    @Test
    public void testColumnsNumber() {
        assertEquals(7, table.getColumnsCount());
    }

    @Test
    public void testGetColumnType() {
        for (int i = 0; i < types.size(); i++) {
            assertEquals(types.get(i), table.getColumnType(i));
        }
    }

    @Test
    public void testRollBack() throws IOException {
        Storeable value = provider.createFor(table);
        assertEquals(0, table.rollback());
        table.put("1", value);
        table.put("2", value);
        table.put("3", value);
        table.remove("1");
        table.put("1", value);
        assertEquals(3, table.size());
        assertEquals(3, table.rollback());
        assertEquals(0, table.size());
    }

    @Test
    public void testCommit() throws IOException {
        Storeable value = provider.createFor(table);
        assertEquals(0, table.commit());
        table.put("1", value);
        table.put("2", value);
        table.put("3", value);
        table.remove("3");
        assertEquals(2, table.commit());
        assertEquals(2, table.size());
        TableProviderFactory factory = new TableProviderFactoryWithCloseAndToString();
        TableProvider provider = factory.create(dbDirPath);
        Table sameTable = provider.getTable("table");
        assertEquals(2, sameTable.size());
    }

    @Test
    public void testCommitAndRollback() throws IOException {
        Storeable value = provider.createFor(table);
        table.put("1", value);
        table.put("2", value);
        table.put("3", value);
        assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        assertNull(table.get("1"));
        assertNull(table.get("2"));
        assertEquals(2, table.rollback());
        table.remove("1");
        assertEquals(1, table.commit());
        assertEquals(2, table.size());
        table.put("1", value);
        assertEquals(3, table.size());
        assertEquals(1, table.rollback());
        assertEquals(2, table.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseTable() throws Exception {
        Storeable value = provider.createFor(table);
        table.put("1", value);
        table.commit();
        ((TableWithCloseAndToString) table).close();
        table.get("1");
    }

    @Test
    public void testCloseTableAndGetNewFromProvider() throws Exception {
        Storeable value = provider.createFor(table);
        table.put("1", value);
        table.commit();
        table.put("2", value);
        ((TableWithCloseAndToString) table).close();
        table = provider.getTable("table");
        assertNotNull(table.get("1"));
        assertNull(table.get("2"));
    }

    @Test
    public void testToString() {
        File directory = new File(dbDirPath);
        File curDirectory = new File(directory, table.getName());
        String path = curDirectory.getPath();
        assertEquals(table.toString(), "TableWithCloseAndToString[" + path + "]");
    }

}
