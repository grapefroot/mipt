package ru.fizteh.fivt.students.SibgatullinDamir.junit;

/**
 * Created by Lenovo on 20.10.2014.
 */
public interface CommandsForTables {
    void execute(String[] args, MultiFileHashMap multiFileHashMap) throws MyException;
    String getName();
}
