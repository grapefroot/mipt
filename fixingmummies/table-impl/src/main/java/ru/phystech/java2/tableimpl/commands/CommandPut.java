package ru.phystech.java2.tableimpl.commands;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.phystech.java2.table.Command;
import ru.phystech.java2.tableimpl.FileMapGlobalState;

import java.io.IOException;
import java.util.List;

@Service
public class CommandPut implements Command {
    private final String name = "put";
    private final int amArgs = 2;

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
        if (workState.getCurrentTable() != null) {
            String key = args.get(1);
            String value = args.get(2);
            String result = workState.put(key, value);
            if (result == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(result);
            }
        } else {
            System.out.println("no table");
        }
    }
}
