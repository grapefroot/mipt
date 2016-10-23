package ru.fizteh.fivt.students.gudkov394.Proxy.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.gudkov394.Proxy.TableProviderFactoryWithCloseAndToString;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestStoreable {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public Table table;
    Storeable storeable;
    List<Class<?>> types;

    @Before
    public void initTable() throws IOException {
        tmpFolder.create();
        TableProviderFactory factory = new TableProviderFactoryWithCloseAndToString();
        String dbDirPath = tmpFolder.newFolder("test").getAbsolutePath();
        TableProvider provider = factory.create(dbDirPath);
        Class<?>[] arrayTypes = {Integer.class, Long.class, Float.class, Double.class,
                Boolean.class, String.class, Byte.class};
        types = Arrays.asList(arrayTypes);
        table = provider.createTable("table", types);
        storeable = provider.createFor(table);
    }

    @After
    public void after() {
        tmpFolder.delete();
    }

    @Test(expected = ColumnFormatException.class)
    public void testSetAndGetColumns() {
        storeable.setColumnAt(0, 3);
        storeable.setColumnAt(1, 3L);

        storeable.setColumnAt(2, 3.2f);
        storeable.setColumnAt(3, 5.4);
        storeable.setColumnAt(4, true);
        storeable.setColumnAt(5, "hello");
        storeable.setColumnAt(6, (byte) 1);
        assertEquals(storeable.getIntAt(0), (Integer) 3);
        assertEquals(storeable.getLongAt(1), (Long) 3L);
        assertEquals(storeable.getFloatAt(2), (Float) 3.2f);
        assertEquals(storeable.getDoubleAt(3), (Double) 5.4);
        assertEquals(storeable.getBooleanAt(4), true);
        assertEquals(storeable.getStringAt(5), "hello");
        assertEquals(storeable.getByteAt(6), (Byte) (byte) 1);
        assertEquals(storeable.getColumnAt(4), true);
        storeable.setColumnAt(3, null);
        assertNull(storeable.getColumnAt(3));
        assertNull(storeable.getDoubleAt(3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        storeable.getColumnAt(7);
    }

    @Test(expected = ColumnFormatException.class)
    public void testGetIncorrectType() {
        storeable.getLongAt(0);
    }

    @Test
    public void testToString() {
        storeable.setColumnAt(0, 3);
        storeable.setColumnAt(1, 3L);
        storeable.setColumnAt(2, 3.2f);
        storeable.setColumnAt(3, 5.4);
        storeable.setColumnAt(4, true);
        storeable.setColumnAt(5, "hello");
        storeable.setColumnAt(6, (byte) 1);
        assertEquals(storeable.toString(), "StoreableWithToString[3,3,3.2,5.4,true,hello,1]");
        storeable.setColumnAt(2, null);
        storeable.setColumnAt(3, null);
        assertEquals(storeable.toString(), "StoreableWithToString[3,3,,,true,hello,1]");
    }
}

