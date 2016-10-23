package ru.phystech.java2.presentation;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import ru.phystech.java2.shell.GenericCmdList;
import ru.phystech.java2.tableimpl.MultiFileDataBaseGlobalState;
import ru.phystech.java2.tableimpl.commands.*;

public class CommandListProvider {
    @Bean
    public GenericCmdList makeUpCmdList(MultiFileDataBaseGlobalState state) {
        GenericCmdList shellCmdList = new GenericCmdList();
        shellCmdList.addCommand(new CommandPut());
        shellCmdList.addCommand(new CommandGet());
        shellCmdList.addCommand(new CommandRemove());
        shellCmdList.addCommand(new CommandUse());
        shellCmdList.addCommand(new CommandCreate());
        shellCmdList.addCommand(new CommandDrop());
        shellCmdList.addCommand(new CommandExit());
        shellCmdList.addCommand(new CommandCommit());
        shellCmdList.addCommand(new CommandRollback());
        shellCmdList.addCommand(new CommandSize());
        return shellCmdList;
    }
}
