package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.InputStream;
import java.io.PrintStream;

public class SizeCommand extends AbstractCommand {

    Table db;

    public SizeCommand(Table t) {
        super("size", 0);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        outputStream.println(db.size());
    }
}
