package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

public abstract class TableAbstractCommand extends AbstractCommand<TableProvider> {

    protected TableAbstractCommand(String name, int argNum, TableProvider context) {
        super(name, argNum, context);
    }

    protected Table getActiveTable() {
        Table retVal = context.getActiveTable();
        if (retVal == null) {
            throw new InputMistakeException("no table");
        }
        return retVal;
    }
}

