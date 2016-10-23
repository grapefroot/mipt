/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.database;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBase;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class CreateCommand extends AbstractCommand<DataBase> {

    public CreateCommand(DataBase context) {
        super("create", 1, context);
    }

    @Override
    public void exec(String[] args) {
        Table table = context.getProvider().createTable(args[0]);
        if (table != null) {
            System.out.println("created");
        } else {
            System.out.printf("%s exists", args[0]);
        }
    }
}
