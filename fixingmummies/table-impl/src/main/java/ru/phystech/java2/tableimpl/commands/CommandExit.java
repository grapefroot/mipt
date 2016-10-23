package ru.phystech.java2.tableimpl.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.phystech.java2.table.Command;
import ru.phystech.java2.tableimpl.FileMapGlobalState;
import ru.phystech.java2.tableimpl.MultiFileDataBaseGlobalState;

import java.io.IOException;
import java.util.List;

@Service
public class CommandExit implements Command {
    private final String name = "exit";
    private final int amArgs = 0;

    @Autowired
    private FileMapGlobalState workState;

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
        /*
        if (workState.autoCommitOnExit) {
            if (workState.getCurrentTable() != null) {
                workState.commit();
            }
        }
        */
        System.exit(0);
    }
}
