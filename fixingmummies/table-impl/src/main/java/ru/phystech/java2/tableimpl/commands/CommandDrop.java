package ru.phystech.java2.tableimpl.commands;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.phystech.java2.table.Command;
import ru.phystech.java2.tableimpl.MultiFileDataBaseGlobalState;
import ru.phystech.java2.utils.CheckOnCorrect;

import java.io.IOException;
import java.util.List;

@Service
public class CommandDrop implements Command {
    private final String name = "drop";
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
        if (!CheckOnCorrect.goodArg(useTableName)) {
            throw new IllegalArgumentException("Bad table name");
        }
        if (!workState.isTableExist(useTableName)) {
            throw new IllegalStateException(useTableName + " not exists");
        } else {
            workState.removeTable(useTableName);
            System.out.println("dropped");
        }
    }
}
