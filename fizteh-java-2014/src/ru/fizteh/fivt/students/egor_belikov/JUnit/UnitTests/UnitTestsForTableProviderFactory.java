package ru.fizteh.fivt.students.egor_belikov.JUnit.UnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.egor_belikov.JUnit.JUnit;
import ru.fizteh.fivt.students.egor_belikov.JUnit.MyTableProviderFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class UnitTestsForTableProviderFactory {
    private final Path testDirectory
            = Paths.get(System.getProperty("user.dir") + File.separator + "db");

    @Before
    public final void setUp() throws Exception {
        testDirectory.toFile().mkdir();
    }

    @Test(expected = IllegalArgumentException.class)
    public final void factoryNullDir() {
        TableProviderFactory test = new MyTableProviderFactory();
        test.create(null);
    }

    @Test
    public final void factoryGood() throws Exception {
        TableProviderFactory test = new MyTableProviderFactory();
        TableProvider testProvider = test.create(testDirectory.toString());
        testProvider.createTable("a");
        assertTrue(testDirectory.resolve("a").toFile().exists());
    }
    @After
    public final void tearDown() throws Exception {
        try {
            for (File currentFile : testDirectory.toFile().listFiles()) {
                if (currentFile.isDirectory()) {
                    for (File subFile : currentFile.listFiles()) {
                        subFile.delete();
                    }
                }
                currentFile.delete();
            }
            testDirectory.toFile().delete();
        } catch (Exception e) {
            assertTrue(false);
        }
        JUnit.execute("exit");
    }
}
