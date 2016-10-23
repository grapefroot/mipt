package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.students.deserg.junit.DbTable;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;
import ru.fizteh.fivt.students.deserg.junit.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableGet implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                System.out.println("no table");
                return;
            }

            String key = args.get(1);
            String value = table.get(key);
            if (value != null) {
                System.out.println("found\n" + value);
            } else {
                System.out.println("not found");
            }

        } else {
            System.out.println("Too many arguments");
        }

    }

}
