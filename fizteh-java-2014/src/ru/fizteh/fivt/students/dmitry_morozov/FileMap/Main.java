package ru.fizteh.fivt.students.dmitry_morozov.filemap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    /**
     * @param args
     * @throws IOException
     */
    public static boolean functionHandler(String[] comAndParams, int bIndex,
            int eIndex, FileMap fm) throws IOException { // Returns false if
                                                         // exit command got.
        boolean res = true;

        String com = comAndParams[bIndex];
        if (com.equals("exit")) {
            res = true;
            try {
                fm.exit();
            } catch (IOException e) {
                throw e;
            }
        } else if (com.equals("put")) {
            if (bIndex + 3 > eIndex) {
                System.out.println("Not enough parametres for put");
            } else {
                System.out.println(fm.put(comAndParams[bIndex + 1],
                        comAndParams[bIndex + 2]));
            }
        } else if (com.equals("remove")) {
            if (bIndex + 2 > eIndex) {
                System.out.println("Not enough parametres for remove");
            } else {
                System.out.println(fm.remove(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("get")) {
            if (bIndex + 2 > eIndex) {
                System.out.println("Not enough parametres for get");
            } else {
                System.out.println(fm.get(comAndParams[bIndex + 1]));
            }
        } else if (com.equals("list")) {
            PrintWriter pw = new PrintWriter(System.out);
            fm.list(pw);
        } else {
            System.out.println("Command not found");
        }

        return res;
    }

    public static boolean commandSplitting(String command, FileMap fm)
            throws IOException { // Returns false if exit command got.
        String[] firstSplitted = command.split(" ");
        String[] toGive = new String[firstSplitted.length];
        int j = 0;
        for (int i = 0; i < firstSplitted.length; i++) {
            if (firstSplitted[i].length() > 0) {
                toGive[j] = firstSplitted[i];
                j++;
            }
        }
        if (0 == j) {
            return true;
        }
        return functionHandler(toGive, 0, j, fm);
    }

    public static void batchMode(String[] args, String path) throws IOException {
        if (null == path) {
            System.err.println("System property db.file is undefined");
            System.exit(1);
        }
        String currentLine = "";
        for (int i = 0; i < args.length; i++) {
            currentLine += args[i] + " ";
        }
        String[] commands = currentLine.split(";");
        try {
            FileMap fm = new FileMap(path);
            boolean exitCode = true;
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].length() > 0) {
                    exitCode = commandSplitting(commands[i], fm);
                    if (!exitCode) {
                        break;
                    }
                }
            }
            if (exitCode) {
                fm.exit();
            }
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        System.exit(0);
    }

    public static void main(String[] args) throws IOException {
        String path = System.getProperty("db.file");
        if (0 != args.length) {
            batchMode(args, path);
        }
        Scanner in = new Scanner(System.in);
        if (path == null) {
            System.out.println("System property db.file is undefined");
        }
        try {
            FileMap fm = new FileMap(path);
            boolean contFlag = true;
            while (contFlag) {
                System.out.print("$ ");
                String currentLine = in.nextLine();
                String[] commands = currentLine.split(";");
                for (int i = 0; i < commands.length; i++) {
                    if (commands[i].length() > 0) {
                        contFlag = commandSplitting(commands[i], fm);
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            in.close();
        }
    }
}
