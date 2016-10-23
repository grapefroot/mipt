package ru.phystech.java2.presentation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.phystech.java2.shell.GenericCmdList;
import ru.phystech.java2.shell.GenericShell;
import ru.phystech.java2.shell.ShellLogic;
import ru.phystech.java2.storeable.StoreableDataBaseGlobalState;
import ru.phystech.java2.storeable.StoreableTableProviderFactory;
import ru.phystech.java2.structured.TableProvider;


@Service
public class StoreableShell extends GenericShell {
    private static Logger logger = Logger.getLogger(StoreableShell.class);
    private final Integer parserAndExecutor = 2;
    @Autowired
    GenericCmdList cmdList;

    @Value("${db.path}")
    String workingDirectory;


    public StoreableShell() {
        if (workingDirectory == null) {
            workingDirectory = System.getProperty("fizteh.db.dir");
        }
        try {
            StoreableTableProviderFactory tableProviderFactory = new StoreableTableProviderFactory();
            TableProvider tableProvider = tableProviderFactory.create(workingDirectory);
            StoreableDataBaseGlobalState state = new StoreableDataBaseGlobalState(tableProvider);
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            logger.fatal(exc.getMessage(), exc);
            System.exit(1);
        }
    }


    public void runShell(String[] args) {
        ShellLogic shellLogic = new ShellLogic();
        if (args.length == 0) {
            shellLogic.interactiveMode(System.in, System.out, System.err, parserAndExecutor);
        } else {
            shellLogic.packageMode(args, System.out, System.err, parserAndExecutor);
        }
    }
}
