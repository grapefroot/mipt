package ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

/**
 * Created by Hoderu on 09.10.14.
 */
public interface CommandProcess{
    void process(CommandContainer commandFromString) throws UnknownCommand;
}
