package ru.fizteh.fivt.students.VasilevKirill.junit.multimap.db.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public class RmCommand implements Command {
    @Override
    public boolean checkArgs(String[] args) {
        return false;
    }

    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (args.length < 2) {
            return 1;
        }
        if (args[1] == null) {
            return 1;
        }
        if (args[1].equals("-r")) {
            if (args[2] == null) {
                return 1;
            }
            if (args.length > 3) {
                return 1;
            }
            String filePath;
            if (!args[2].contains(File.separator)) {
                filePath = Shell.currentPath + File.separator + args[2];
            } else {
                filePath = args[2];
            }
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[2] + "': No such file or directory\"");
                return 1;
            }
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (File f : listFiles) {
                    String[] newArgs = {"rm", "-r", f.getName()};
                    if (rmRecursive(newArgs, Shell.currentPath + File.separator + file.getName()) != 0) {
                        return 1;
                    }
                }
                if (!file.delete()) {
                    return 1;
                }
            } else {
                if (!file.delete()) {
                    return 1;
                }
            }
        } else {
            if (args.length > 2) {
                return 1;
            }
            String filePath;
            if (!args[1].contains(File.separator)) {
                filePath = Shell.currentPath + File.separator + args[1];
            } else {
                filePath = args[1];
            }
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
                return 1;
            }
            if (file.isDirectory()) {
                System.out.println("rm: " + args[1] + ": is a directory");
                return 1;
            }
            if (!file.delete()) {
                return 1;
            }
        }
        return 0;
    }

    private int rmRecursive(String[] args, String directory) {
        if (args.length < 2) {
            return 0;
        }
        if (args[1].equals("-r")) {
            File file = new File(directory + File.separator + args[2]);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[2] + "': No such file or directory\"");
                return 1;
            }
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                if (listFiles.length == 0) {
                    if (!file.delete()) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else {
                    for (File f : listFiles) {
                        String[] newArgs = {"rm", "-r", f.getName()};
                        rmRecursive(newArgs, directory + File.separator + file.getName());
                    }
                }
                if (!file.delete()) {
                    return 1;
                }
                return 0;
            } else {
                if (!file.delete()) {
                    return 1;
                }
                return 0;
            }
        } else {
            File file = new File(Shell.currentPath + File.separator + args[1]);
            if (!file.exists()) {
                System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
                return 1;
            }
            if (file.isDirectory()) {
                System.out.println("rm: " + args[1] + ": is a directory");
                return 1;
            }
            if (!file.delete()) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "rm";
    }
}
