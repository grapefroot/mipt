package ru.fizteh.fivt.students.vadim_mazaev;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DbTable;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManager;
import ru.fizteh.fivt.students.vadim_mazaev.DataBase.TableManagerFactory;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.Command;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.Interpreter;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.StopLineInterpretationException;

public final class Main {
    public static void main(String[] args) {
        String dbDirPath = System.getProperty("fizteh.db.dir");
        if (dbDirPath == null) {
            System.err.println("You must specify fizteh.db.dir via -Dfizteh.db.dir JVM parameter");
            System.exit(1);
        }
        TableProviderFactory factory = new TableManagerFactory();
        DataBaseState state = null;
        try {
            state = new DataBaseState(factory.create(dbDirPath));
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        run(state, args);
    }
    
    private static void run(DataBaseState state, String[] args) {
        Interpreter dbInterpreter = new Interpreter(state, new Command[] {
                new Command("put", 2, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            String oldValue = link.put(args[0], args[1]);
                            if (oldValue != null) {
                                System.out.println("overwrite");
                                System.out.println(oldValue);
                            } else {
                                System.out.println("new");
                            }
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("get", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            String value = link.get(args[0]);
                            if (value != null) {
                                System.out.println("found");
                                System.out.println(value);
                            } else {
                                System.out.println("not found");
                            }
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("remove", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            String removedValue = link.remove(args[0]);
                            if (removedValue != null) {
                                System.out.println("removed");
                            } else {
                                System.out.println("not found");
                            }
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("list", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            System.out.println(String.join(", ", link.list()));
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("size", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            System.out.println(link.size());
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("commit", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            System.out.println(link.commit());
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("rollback", 0, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        Table link = ((DataBaseState) state).getUsedTable();
                        if (link != null) {
                            System.out.println(link.rollback());
                        } else {
                            throw new StopLineInterpretationException("no table");
                        }
                    }
                }),
                new Command("create", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        TableProvider manager = ((DataBaseState) state).getManager();
                        if (manager.createTable(args[0]) != null) {
                            System.out.println("created");
                        } else {
                            throw new StopLineInterpretationException(args[0] + " exists");
                        }
                    }
                }),
                new Command("use", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        DataBaseState dbState = ((DataBaseState) state);
                        TableProvider manager = dbState.getManager();
                        Table newTable = manager.getTable(args[0]);
                        DbTable usedTable = (DbTable) dbState.getUsedTable();
                        if (newTable != null) {
                            if (usedTable != null && (usedTable.getNumberOfChanges() > 0)) {
                                System.out.println(usedTable.getNumberOfChanges()
                                        + " unsaved changes");
                            } else {
                                dbState.setUsedTable(newTable);
                                System.out.println("using " + args[0]);
                            }
                        } else {
                            throw new StopLineInterpretationException(args[0] + " not exists");
                        }
                    }
                }),
                new Command("drop", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        DataBaseState dbState = ((DataBaseState) state);
                        TableProvider manager = dbState.getManager();
                        Table usedTable = dbState.getUsedTable();
                        if (usedTable != null && usedTable.getName().equals(args[0])) {
                            dbState.setUsedTable(null);
                        }
                        try {
                            manager.removeTable(args[0]);
                            System.out.println("dropped");
                        } catch (IllegalStateException e) {
                            throw new StopLineInterpretationException("tablename not exists");
                        }
                    }
                }),
                new Command("show", 1, new BiConsumer<Object, String[]>() {
                    @Override
                    public void accept(Object state, String[] args) {
                        if (args[0].equals("tables")) {
                            DataBaseState dbState = ((DataBaseState) state);
                            TableManager manager = (TableManager) dbState.getManager();
                            List<String> tableNames = manager.getTablesList();
                            System.out.println("table_name row_count");
                            for (String name : tableNames) {
                                Table curTable = manager.getTable(name);
                                System.out.println(curTable.getName() + " " + curTable.size());
                            }
                        } else {
                            throw new StopLineInterpretationException(Interpreter.NO_SUCH_COMMAND_MSG
                                    + "show " + args[0]);
                        }
                    }
                })
        });
        dbInterpreter.setExitHandler(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                DbTable link = (DbTable) state.getUsedTable();
                if (link != null && (link.getNumberOfChanges() > 0)) {
                    System.out.println(link.getNumberOfChanges()
                            + " unsaved changes");
                    return false;
                }
                return true;
            }
            
        });
        
        try {
            System.exit(dbInterpreter.run(args));
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                System.err.println("Something went wrong. Unexpected error");
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
