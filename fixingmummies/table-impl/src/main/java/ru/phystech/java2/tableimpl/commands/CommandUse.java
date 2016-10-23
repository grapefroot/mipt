package ru.phystech.java2.tableimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.phystech.java2.table.Command;
import ru.phystech.java2.tableimpl.MultiFileDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

@Service
public class CommandUse implements Command {
    private final String name = "use";
    private final int amArgs = 1;

    @Autowired
    private MultiFileDataBaseGlobalState workState;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAmArgs() {
        return amArgs;
    }

    @Override
    public void work(List<String> args) throws IOException {
        String useTableName = args.get(1);
        if (!workState.isTableExist(useTableName)) {
            System.err.println(useTableName + " not exists");
        } else {
            if (workState.getCurrentTable() != null) {
                int amChanges = workState.amountOfChanges();
                if (amChanges != 0) {
                    System.err.println(amChanges + " unsaved changes");
                    return;
                }
            }
            workState.setCurrentTable(useTableName);
            System.out.println("using " + useTableName);
        }
    }
}
