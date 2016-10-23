package ru.fizteh.fivt.students.artem_gritsay.FileMap;

import java.io.IOException;
import java.util.HashMap;


public class FileMap {

    public static void main(String[] args) {
        String fileMapPath = System.getProperty("db.file");
        if (fileMapPath == null) {
            System.exit(1);
        }
        Reader reader = new Reader(fileMapPath);
        Writer writer = new Writer(fileMapPath);
        HashMap<String, String> fileMap;
        try {
            fileMap = reader.readDataFromFile();
            Launcher launcher = new Launcher(fileMap, writer);
            if (!launcher.launch(args)) {
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
}
