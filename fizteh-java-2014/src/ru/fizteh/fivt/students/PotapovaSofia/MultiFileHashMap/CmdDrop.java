package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

public class CmdDrop implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) throws IOException {
        if (!db.getDataBase().containsKey(args.get(1))) {
            System.out.println(args.get(1) + " does not exists");
        } else {
            String tableName = args.get(1);
            Path tablePath = db.getDbPath().resolve(tableName);
            File[] tableDirectories = tablePath.toFile().listFiles();
            for (File dir : tableDirectories) {
                File[] tableFiles = dir.listFiles();
                for (File file : tableFiles) {
                    Files.delete(file.toPath());
                }
                Files.delete(dir.toPath());
            }
            Files.delete(tablePath);
            db.getDataBase().remove(args.get(1));
            System.out.println("dropped");
                /*
                try {
                    File[] tableDirectories = tablePath.toFile().listFiles();
                    for (File dir : tableDirectories) {
                        try {
                            File[] tableFiles = dir.listFiles();
                            for (File file : tableFiles) {
                                try {
                                    Files.delete(file.toPath());
                                } catch (IOException | SecurityException e) {
                                    System.err.println(e);
                                }
                            }
                            Files.delete(dir.toPath());
                        } catch (IOException | SecurityException e) {
                            System.err.println(e);
                        }
                    }
                    try {
                        Files.delete(tablePath);
                    } catch (IOException | SecurityException e) {
                        System.err.println("Can't delete empty directory");
                    }
                    db.getDataBase().remove(args.get(1));
                    System.out.println("dropped");
                } catch (SecurityException e) {
                    System.err.println(e);
                }
                */
        }
    }
}
