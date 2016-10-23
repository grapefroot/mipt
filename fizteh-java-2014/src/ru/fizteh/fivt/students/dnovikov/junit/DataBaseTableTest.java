package ru.fizteh.fivt.students.dnovikov.junit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class DataBaseTableTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public DataBaseTable table;
    public String dbDirPath;

    @Before
    public void setUp() throws IOException {
        TableProviderFactory factory = new DataBaseProviderFactory();
        dbDirPath = tmpFolder.newFolder().getAbsolutePath();
        TableProvider provider = factory.create(dbDirPath);
        table = (DataBaseTable) provider.createTable("table");
    }

    @Test
    public void testGetName() {
        assertEquals("table", table.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void putWithNullArgumentsThrowsException() {
        table.put(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getWithNullArgumentThrowsException() {
        table.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeWithNullArgumentThrowsException() {
        table.remove(null);
    }

    @Test
    public void testPutAndGet() {
        assertNull(table.put("1", "2"));
        assertEquals("2", table.get("1"));
        assertEquals("2", table.put("1", "3"));
        assertEquals("3", table.get("1"));
        assertNull(table.get("a"));
    }


    @Test
    public void testPutAndRemove() {
        assertNull(table.put("1", "2"));
        assertNull(table.remove("2"));
        assertEquals("2", table.remove("1"));
        assertNull(table.remove("1"));
        assertNull(table.get("1"));
    }

    @Test
    public void testSize() {
        assertEquals(0, table.size());
        table.put("1", "2");
        assertEquals(1, table.size());
        table.put("3", "4");
        assertEquals(2, table.size());
        table.put("3", "5");
        assertEquals(2, table.size());
        table.remove("1");
        assertEquals(1, table.size());
        table.remove("1");
        assertEquals(1, table.size());
        table.remove("3");
        assertEquals(0, table.size());
    }

    @Test
    public void testList() {
        assertEquals(0, table.list().size());
        table.put("1", "2");
        table.put("3", "4");
        table.put("3", "5");
        table.remove("1");
        table.put("6", "7");
        table.put("key", "value");
        assertEquals(3, table.list().size());
        assertTrue(table.list().containsAll(new LinkedList<>(Arrays.asList("3", "6", "key"))));
    }

    @Test
    public void testRollBack() {
        assertEquals(0, table.rollback());
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        table.remove("1");
        table.put("1", "5");
        assertEquals(3, table.size());
        assertEquals(3, table.rollback());
        assertEquals(0, table.size());
    }

    @Test
    public void testCommit() throws IOException {
        assertEquals(0, table.commit());
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        table.remove("3");
        assertEquals(2, table.commit());
        assertEquals(2, table.size());
        TableProviderFactory factory = new DataBaseProviderFactory();
        TableProvider provider = factory.create(dbDirPath);
        Table sameTable = provider.getTable("table");
        assertEquals(2, sameTable.size());
    }

    @Test
    public void testCommitAndRollback() {
        table.put("1", "2");
        table.put("2", "3");
        table.put("3", "4");
        assertEquals(3, table.commit());
        table.remove("1");
        table.remove("2");
        assertNull(table.get("1"));
        assertNull(table.get("2"));
        assertEquals(2, table.rollback());
        assertEquals("2", table.get("1"));
        assertEquals("3", table.get("2"));
        table.remove("1");
        assertEquals(1, table.commit());
        assertEquals(2, table.size());
        table.put("1", "2");
        assertEquals(3, table.size());
        assertEquals(1, table.rollback());
        assertEquals(2, table.size());
    }

    @Test
    public void testGetUnsavedChanges() {
        table.put("1", "2");
        assertEquals(1, table.getNumberOfChanges());
        table.put("1", "4");
        assertEquals(1, table.getNumberOfChanges());
        table.commit();
        table.put("1", "4");
        table.put("1", "4");
        assertEquals(0, table.getNumberOfChanges());
        table.put("2", "3");
        assertEquals(1, table.getNumberOfChanges());
        table.remove("1");
        table.remove("2");
        assertEquals(1, table.getNumberOfChanges());
    }
}
