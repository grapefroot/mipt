package ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.utils;

import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.Main;
import ru.fizteh.fivt.students.irina_karatsapova.multifilehashmap.table.Table;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static String concatStrings(String[] args, String separator) {
        StringBuilder sb = new StringBuilder();
        if (args.length == 0) {
            return "";
        }
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (args[i] != null) {
                sb.append(separator).append(args[i]);
            }
        }
        return sb.toString();
    }

    public static String mainDir() {
        return System.getProperty(Main.mainDir);
    }

    public static File makePathAbsolute(String str) {
        Path path = Paths.get(str);
        if (!path.isAbsolute()) {
            path = Paths.get(Utils.mainDir(), path.toString());
        }
        return path.normalize().toFile();
    }

    public static void delete(File file) throws TableException {
        if (!file.delete()) {
            throw new TableException(file.toString() + ": Can't delete");
        }
    }

    public static void rmdirs(File removed) throws Exception {
        if (removed.isDirectory()) {
            for (File object: removed.listFiles()) {
                rmdirs(object);
            }
        }
        Utils.delete(removed);
    }

    public static boolean checkTableLoading() {
        if (!Table.loaded) {
            System.out.println("no table");
            return false;
        }
        return true;
    }
}



