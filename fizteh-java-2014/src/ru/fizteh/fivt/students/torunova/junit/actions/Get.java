package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class Get extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
            String value = currentTable.get().get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "get";
    }
}

