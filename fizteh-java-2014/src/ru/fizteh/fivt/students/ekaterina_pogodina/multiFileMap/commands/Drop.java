package ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.commands;

import ru.fizteh.fivt.students.ekaterina_pogodina.multiFileMap.TableManager;

public class Drop extends Command {
    @Override
    public void execute(String[] args, TableManager table) throws Exception {
        boolean tableDrop = table.drop(args[1]);
    }
}
