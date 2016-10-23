package ru.fizteh.fivt.students.Oktosha.ConsoleUtility;

/**
 * Parent class for all exceptions thrown by ConsoleUtility
 */
public class ConsoleUtilityException extends Exception {
    public ConsoleUtilityException(String s) {
        super(s);
    }
    public ConsoleUtilityException(String s, Exception e) {
        super(s, e);
    }
}
