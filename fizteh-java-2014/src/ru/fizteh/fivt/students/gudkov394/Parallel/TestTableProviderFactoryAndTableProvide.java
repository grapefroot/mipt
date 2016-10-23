package ru.fizteh.fivt.students.gudkov394.Parallel;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.gudkov394.Storable.src.Junit;
import ru.fizteh.fivt.students.gudkov394.Storable.src.Utils;

import java.io.IOException;


/**
 * Created by kagudkov on 20.10.14.
 */
public class TestTableProviderFactoryAndTableProvide {
    ParallelTableProvider provider;
    ParallelTableProviderFactory factory;
    Utils utils = new Utils();

    @Before
    public void beforeTest() throws IOException {
        provider = (ParallelTableProvider)
                new ParallelTableProviderFactory().create("/home/kagudkov/fizteh-java-2014/test1");
        provider.createTable("table1", utils.signature("int int"));
        provider.createTable("table2", utils.signature("int int"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionDirIsEqualsNull() {
        Junit factory = new Junit();
        factory.create(null);
    }

    @Test
    public void testGetTable() throws Exception {
// non-existing tables
        Assert.assertNull(provider.getTable("NonExistingTable"));
        Assert.assertNull(provider.getTable("ThereIsNoSuchTable"));
// existing tables
        Assert.assertNotNull(provider.getTable("table1"));
        Assert.assertNotNull(provider.getTable("table2"));
    }

    @Test
    public void testCreateTable() throws Exception {
// non-existing table
        Assert.assertNotNull(provider.createTable("newTable1", utils.signature("int long")));
        Table table = provider.getTable("newTable1");
        table.put("1", provider.deserialize(table, "[1,1]"));
        table.commit();
// existing tables
        Assert.assertNull(provider.createTable("table1", utils.signature("int long")));
        Assert.assertNull(provider.createTable("table2", utils.signature("int long")));
// clean-up
        provider.removeTable("newTable1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateTableExceptions() throws IOException {
        provider.createTable(null, utils.signature("int long"));
    }

    @Test
    public void testRemoveTable() throws Exception {
//prepare
        provider.createTable("newTable1", utils.signature("int long"));
        Table table = provider.getTable("newTable1");
        table.put("1", provider.deserialize(table, "[1,1]"));
        table.commit();
// existing tables
        provider.removeTable("newTable1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTableIllegalArgumentException() throws IOException {
        provider.removeTable(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveTableIllegalStateException() throws IOException {
        provider.removeTable("nonExistingTable");
        provider.removeTable("nosuchtable");
    }

    @Test
    public void testInitTable() throws Exception {
// non-existing table
        Assert.assertNotNull(provider.createTable("newTable2", utils.signature("int long String")));
        ParallelTable table = (ParallelTable) provider.getTable("newTable2");
        table.put("1", provider.deserialize(table, "[1,1,\"Harry\"]"));
        table.commit();
// existing tables
        provider.removeTable("newTable2");

    }
}
