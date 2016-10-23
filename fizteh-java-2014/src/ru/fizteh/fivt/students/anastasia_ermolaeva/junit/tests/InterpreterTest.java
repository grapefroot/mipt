package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Command;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.ExitException;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.Interpreter;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util.TableState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class InterpreterTest {
    private final String newLine = System.getProperty("line.separator");
    private final String testCommand = "command";
    private final String testOutput = "TEST";
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errputStream;
    private PrintStream printStream;
    private PrintStream printErrStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        errputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        printErrStream = new PrintStream(errputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpreterThrowsIllegalArgumentExceptionIfGivenNullStreams() {
        new Interpreter(null, new Command[]{}, null, null, null);
    }

    @Test
    public void testRunValidUserMode() {
        Interpreter test = new Interpreter(null, new Command[]{
                new Command("command", 1, (TableState tableS, String[] arguments) -> printStream.println(testOutput))},
                new ByteArrayInputStream(
                        (testCommand + newLine).getBytes()), printStream, printErrStream);
        try {
            test.run(new String[]{});
        } catch (ExitException e) {

            assertEquals(0, e.getStatus());

            assertEquals(test.PROMPT + testOutput + newLine + test.PROMPT,
                   outputStream.toString());
        }

    }

    @Test
    public void testRunValidBatchMode() {
        Interpreter test = new Interpreter(null, new Command[]{
                new Command("command", 1, (TableState tableS, String[] arguments) -> printStream.println(testOutput))},
                new ByteArrayInputStream(new byte[]{}), printStream, printErrStream);
        try {
            test.run(new String[]{testCommand + test.STATEMENT_DELIMITER, testCommand});
        } catch (ExitException e) {

            assertEquals(0, e.getStatus());

            assertEquals(testOutput + newLine + testOutput + newLine, outputStream.toString());
        }
    }

    @Test
    public void testBatchModePrintErrorMessageInErrStreamForUnexpectedCommand() {
        Interpreter test = new Interpreter(null, new Command[]{},
                new ByteArrayInputStream(new byte[]{}), printStream, printErrStream);
        try {
            test.run(new String[]{testCommand});
        } catch (ExitException e) {

            assertNotEquals(0, e.getStatus());

            assertEquals(test.ERR_MSG + testCommand + newLine, outputStream.toString());
        }
    }

    @Test
    public void testUserModeWriteErrorInStreamForUnexpectedCommand() {
        Interpreter test = new Interpreter(null, new Command[]{},
                new ByteArrayInputStream((testCommand + newLine).getBytes()), printStream, printErrStream);
        try {
            test.run(new String[]{});
        } catch (ExitException e) {

            assertEquals(0, e.getStatus());

            assertEquals(test.PROMPT + test.ERR_MSG
                    + testCommand + newLine + test.PROMPT, outputStream.toString());
        }
    }

    @Test
    public void testBatchModePrintErrorMessageInErrStreamForCommandWithWrongNumberOfArguments() {
        Interpreter test = new Interpreter(null, new Command[]{
                new Command("command", 1, (TableState tableS, String[] arguments) -> printStream.println(testOutput))},
                new ByteArrayInputStream(new byte[]{}), printStream, printErrStream);
        try {
            test.run(new String[]{testCommand + " argument"});
        } catch (ExitException e) {

            assertEquals("command: invalid number of arguments: 0 expected, 1 found."
            + newLine, outputStream.toString());

            assertNotEquals(0, e.getStatus());
        }
    }
    @Test
    public void testUserModePrintErrorMessageInOutStreamForCommandWithWrongNumberOfArguments() {
        Interpreter test = new Interpreter(null, new Command[]{
                new Command("command", 1, (TableState tableS, String[] arguments) -> printStream.println(testOutput))},
                new ByteArrayInputStream((testCommand + " argument").getBytes()), printStream, printErrStream);
        try {
            test.run(new String[]{});
        } catch (ExitException e) {

            assertEquals(test.PROMPT
            + "command: invalid number of arguments: 0 expected, 1 found."
            + newLine + test.PROMPT,
                    outputStream.toString());
        }
    }
    @After
    public void tearDown() throws IOException {
        outputStream.close();
        printStream.close();
        errputStream.close();
        printErrStream.close();
    }
}
