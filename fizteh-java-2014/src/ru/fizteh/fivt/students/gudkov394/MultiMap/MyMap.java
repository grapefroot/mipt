package ru.fizteh.fivt.students.gudkov394.MultiMap;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class MyMap {
    public Map<String, CurrentTable> tables = new HashMap<String, CurrentTable>();
    public CurrentTable ct = new CurrentTable();

    public Boolean checkName(final String name) {
        Map<String, Integer> mapStrign = new HashMap<String, Integer>();
        mapStrign.put("put", 0);
        mapStrign.put("get", 1);
        mapStrign.put("remove", 2);
        mapStrign.put("list", 3);
        mapStrign.put("exit", 4);
        mapStrign.put("create", 4);
        mapStrign.put("use", 4);
        mapStrign.put("drop", 4);
        mapStrign.put("size", 4);
        mapStrign.put("#*#", 4);
        return mapStrign.containsKey(name);
    }

    private void initMap() {
        File f = new File(System.getProperty("fizteh.db.dir"));
        String[] s = f.list();
        if (s != null) {
            for (String tmp : s) {
                CurrentTable ct = new CurrentTable(tmp);
                ct.init(); //в number запишем количество аргументов, а дальше поддерживаем его
                ct.clear();
                tables.put(tmp, ct);
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
            createTable(currentArgs[1]);
        } else if ("use".equals(currentArgs[0])) {
            if (currentArgs.length != 2) {
                System.err.println("wrong number of argument to use");
                System.exit(1);
            }
            if (tables.containsKey(currentArgs[1])) {
                ct.clear();
                ct = tables.get(currentArgs[1]);
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
            removeTable(currentArgs[1]);
            if (tables.containsKey(currentArgs[1])) {
                tables.get(currentArgs[1]).delete();
                tables.remove(currentArgs[1]);
            } else {
                System.out.println("tablename not exists");
            }
        } else if ("#*#".equals(currentArgs[0])) {
            Set<String> set = tables.keySet();
            System.out.println("table_name row_count");
            for (String s : set) {
                System.out.println(s + " " + ((Integer) tables.get(s).size()).toString());
            }
            System.out.println();
        } else if ("size".equals(currentArgs[0])) {
            if (currentArgs.length != 1) {
                System.err.println("wrong number of argument to drop");
                System.exit(1);
            }
            int size = 0;
            for (String s : tables.keySet()) {
                size += tables.get(s).size();
            }
            System.out.println(size);
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

    public CurrentTable getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            return null;
        }
        return tables.get(name);
    }

    public CurrentTable createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            CurrentTable newTable = new CurrentTable(name);
            tables.put(name, newTable);
            return newTable;
        }
    }

    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            tables.get(name).delete();
            tables.remove(name);
        } else {
            throw new IllegalStateException();
        }
    }
}
