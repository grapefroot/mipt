package ru.fizteh.fivt.students.irina_karatsapova.storeable.commands;

import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

public class DropCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!Utils.checkNoChanges(tableProvider)) {
            return;
        }
        String tableName = args[1];
        try {
            tableProvider.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(tableName + "not exists");
        }
    }

    public String name() {
        return "drop";
    }

    public int minArgs() {
        return 2;
    }

    public int maxArgs() {
        return 2;
    }
}
