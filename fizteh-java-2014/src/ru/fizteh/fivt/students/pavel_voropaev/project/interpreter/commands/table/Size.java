package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Size extends TableAbstractCommand {

    public Size(TableProvider context) {
        super("size", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        out.println(super.getActiveTable().size());
    }
}
