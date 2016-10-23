package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class UnknownCommand implements ShellCommand {

    private String args;

    public UnknownCommand(String args) {
        this.args = args;
    }

    @Override
    public boolean execute() {
        System.out.println(args + ": Command not found");
        return false;
    }

}
