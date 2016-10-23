package ru.fizteh.fivt.students.gudkov394.Junit.src;


import java.io.File;
import java.util.Scanner;
import java.util.Set;

public class MyMap {
    public CurrentTable ct = new CurrentTable();
    TableProviderClass tableProviderClass = new TableProviderClass();

    public Boolean checkName(final String name) {
        String[] s = {"put", "get", "remove", "list", "exit", "create", "use", "drop", "#*#",
                "size", "commit", "rollback"};
        for (int i = 0; i < s.length; ++i) {
            if (name.equals(s[i])) {
                return true;
            }
        }
        return false;
    }

    private void initMap() {
        File f = new File(System.getProperty("fizteh.db.dir"));
        String[] s = f.list();
        if (s != null) {
            for (String tmp : s) {
                CurrentTable ct = new CurrentTable(tmp);
                ct.init(); //в number запишем количество аргументов, а дальше поддерживаем его
                ct.clear();
                tableProviderClass.put(tmp, ct);
            }
        }
    }

    boolean obvious() {
        if (ct.getName() == null) {
            System.err.println("no table");
            return false;
        }
        return true;
    }

    public void run(final String[] currentArgs) {
        if ("put".equals(currentArgs[0])) {
            if (obvious()) {
                Put put = new Put(currentArgs, ct);
            }
        } else if ("get".equals(currentArgs[0])) {
            if (obvious()) {
                Get get = new Get(currentArgs, ct);
            }
        } else if ("remove".equals(currentArgs[0])) {
            if (obvious()) {
                if (currentArgs.length != 2) {
                    System.err.println("wrong number of argument to Create");
                    System.exit(1);
                }
                ct.remove(currentArgs[1]);
            }
        } else if ("list".equals(currentArgs[0])) {
            if (obvious()) {
                ListTable listTable = new ListTable(currentArgs, ct);
            }
        } else if ("exit".equals(currentArgs[0])) {
            Exit exit = new Exit(currentArgs);
        } else if ("create".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to Create");
                System.exit(1);
            }
            tableProviderClass.createTable(currentArgs[1]);
        } else if ("use".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to use");
                System.exit(1);
            }
            if (tableProviderClass.tables.containsKey(currentArgs[1])) {
                ct.clear();
                ct = tableProviderClass.tables.get(currentArgs[1]);
                ct.init();
                System.out.println("using " + ct.getName());
            } else {
                System.out.println("tablename not exists");
            }
        } else if ("drop".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            tableProviderClass.removeTable(currentArgs[1]);
            if (tableProviderClass.tables.containsKey(currentArgs[1])) {
                tableProviderClass.tables.get(currentArgs[1]).delete();
                tableProviderClass.tables.remove(currentArgs[1]);
            } else {
                System.out.println("tablename not exists");
            }
        } else if ("#*#".equals(currentArgs[0])) {
            Set<String> set = tableProviderClass.tables.keySet();
            System.out.println("table_name row_count");
            for (String s : set) {
                System.out.println(s + " " + ((Integer) tableProviderClass.tables.get(s).size()).toString());
            }
            System.out.println();
        } else if ("size".equals(currentArgs[0])) {
            if (currentArgs.length != 1) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            int size = 0;
            for (String s : tableProviderClass.tables.keySet()) {
                size += tableProviderClass.tables.get(s).size();
            }
            System.out.println(size);
        } else if ("commit".equals(currentArgs[0])) {
            if (currentArgs.length != 1) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            System.out.println(ct.commit());
        } else if ("rollback".equals(currentArgs[0])) {
            if (currentArgs.length != 1) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            System.out.println(ct.rollback());
        } else {
            System.err.println("wrong command");
            System.exit(22);
        }
    }

    public void interactive() {
        Scanner sc = new Scanner(System.in);
        CurrentTable currentTable = new CurrentTable();
        initMap();
        while (true) {

            String currentString = sc.nextLine();
            currentString = currentString.trim();
            currentString = currentString.replaceAll("\\s*;\\s*", ";");
            currentString = currentString.replaceAll("\\s+", " ");
            currentString = currentString.replaceAll("show tables", "#*#");
            String[] arrayCommands = currentString.split(";");
            for (int j = 0; j < arrayCommands.length; ++j) {
                run(arrayCommands[j].trim().split("\\s+"));
            }
        }

    }

    public void packageMode(final String[] args) {
        Scanner sc = new Scanner(System.in);
        CurrentTable currentTable = new CurrentTable();
        initMap();
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        string = string.replaceAll("\\s*;\\s*", ";");
        string = string.replaceAll("\\s+", " ");
        string = string.replaceAll("show tables", "#*#");
        String[] commands = string.split(";|(\\s+)");
        int i = 0;
        while (i < commands.length) {
            int first = i;
            ++i;
            while (i < commands.length && !checkName(commands[i])) {
                ++i;
            }
            int size = 0;
            for (int j = 0; j < i - first; ++j) {
                if (commands[j + first].length() != 0) {
                    ++size;
                }
            }

            String[] s = new String[size];
            int tmpSize = 0;
            for (int j = 0; j < s.length; ++j) {
                if (commands[j + first].length() != 0) {
                    s[tmpSize] = commands[j + first];
                    ++tmpSize;
                }
            }
            run(s);
        }
        System.exit(0);
    }


    public MyMap(final String[] args) {
        if (args.length == 0) {
            interactive();
        } else {
            packageMode(args);
        }
    }
}
