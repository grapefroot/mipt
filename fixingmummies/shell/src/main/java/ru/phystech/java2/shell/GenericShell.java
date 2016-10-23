package ru.phystech.java2.shell;

import org.springframework.stereotype.Service;

@Service
public class GenericShell {

    public GenericShell() {
    }

    public GenericShell(String[] args) {
        final Integer parserAndExecutor = 0;
        ShellLogic sh = new ShellLogic();
        if (args.length == 0) {
            sh.interactiveMode(System.in, System.out, System.err, parserAndExecutor);
        } else {
            sh.packageMode(args, System.out, System.err, parserAndExecutor);
        }
    }
}