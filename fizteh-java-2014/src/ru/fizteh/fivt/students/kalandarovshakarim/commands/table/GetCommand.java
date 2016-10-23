/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.table;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.database.OneTableBase;

/**
 *
 * @author shakarim
 */
public class GetCommand extends AbstractTableCommand {

    public GetCommand(OneTableBase context) {
        super("get", 1, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        String value = activeTable.get(args[0]);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println(value);
            System.out.println("found");
        }
    }
}
