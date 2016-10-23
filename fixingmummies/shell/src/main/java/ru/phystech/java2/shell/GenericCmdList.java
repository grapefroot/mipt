package ru.phystech.java2.shell;


import org.springframework.stereotype.Service;
import ru.phystech.java2.table.Command;

import java.util.HashMap;
import java.util.Map;

@Service
public class GenericCmdList {
    private static Map<String, Command> cmdlist = new HashMap<>();

    public GenericCmdList() {
    }

    public Map<String, Command> getCmdList() {
        return cmdlist;
    }

    public void addCommand(Command command) {
        cmdlist.put(command.getName(), command);
    }
}
