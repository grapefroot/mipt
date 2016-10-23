package ru.fizteh.fivt.students.pavel_voropaev.filemap;

public final class Put {
    private static int argNum = 3;
    private static String name = "put";
    private static String usage = "Usage: put <key> <value>";

    public static void exec(Database db, String[] param) throws IllegalArgumentException, IllegalStateException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (param.length < argNum) {
            ThrowExc.notEnoughArg(name, usage);
        }
        String tmp = db.put(param[1], param[2]);
        if (tmp == null) {
            System.out.println("new");
        } else {
            System.out.println("Overwrite");
            System.out.println(tmp);
        }
    }
    
}
