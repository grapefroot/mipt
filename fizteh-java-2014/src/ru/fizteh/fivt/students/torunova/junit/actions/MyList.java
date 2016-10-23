package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;

import java.util.List;

/**
 * Created by nastya on 21.10.14.
 */
public class MyList extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        List<String> keys = currentTable.get().list();
        String result = String.join(", ", keys);
        System.out.println(result);
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }
}
