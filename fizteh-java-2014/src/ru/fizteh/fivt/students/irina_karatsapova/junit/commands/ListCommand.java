package ru.fizteh.fivt.students.irina_karatsapova.junit.commands;

import ru.fizteh.fivt.students.irina_karatsapova.junit.table_provider_factory.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.junit.utils.Utils;

import java.util.List;

public class ListCommand implements Command {
    public void execute(TableProvider tableProvider, String[] args) throws Exception {
        if (!Utils.checkTableChosen(tableProvider)) {
            return;
        }
        List<String> keys = tableProvider.currentTable().list();
        String allKeys = "";
        for (String key: keys) {
            if (allKeys.length() > 0) {
                allKeys += ", ";
            }
            allKeys += key;
        }
        System.out.println(allKeys);
    }

    public String name() {
        return "list";
    }

    public int minArgs() {
        return 1;
    }

    public int maxArgs() {
        return 1;
    }
}
