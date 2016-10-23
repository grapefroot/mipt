package ru.fizteh.fivt.students.YaronskayaLiubov.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CatCommand extends Command {
    boolean execute(String[] args) throws IOException {
        if (args.length != maxNumberOfArguements) {
            System.out.println(name + ": Invalid number of arguements");
            return false;
        }
        String fileName = args[1];
        File curFile = (fileName.charAt(0) == '/') ? new File(fileName)
                : new File(Shell.curDir, fileName);
        if (!curFile.exists()) {
            System.out.println(name + ": " + fileName
                    + ": No such file or directory");
            return false;
        }
        FileInputStream fis = new FileInputStream(curFile);
        Shell.fileCopy(fis, System.out);
        fis.close();
        return true;
    }

    CatCommand() {
        name = "cat";
        maxNumberOfArguements = 2;
    }
}
