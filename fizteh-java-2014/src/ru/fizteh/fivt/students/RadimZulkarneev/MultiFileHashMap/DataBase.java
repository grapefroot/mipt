package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataBase {
    private boolean isChoose;
    private SuperTable sTable;
    private Map<String, Integer> tableRowCount;
    private Path dataBasePath;
    public DataBase() throws MapException, DataBaseCorrupt, TableConnectionException, IOException {
       try {
           dataBasePath = Functions.openDir();
       } catch (NullPointerException ex) {
           System.out.println(System.getProperty("fizteh.db.dir") + ": No input");
           System.exit(1);
       } catch (FileNotFoundException ex) {
           System.out.println(System.getProperty("fizteh.db.dir") + ": is not directory");
           System.exit(1);
       }
       tableRowCount = new HashMap<String, Integer>();
       setRowCount();
       isChoose = false;
    }
    
    
    private void setRowCount() throws MapException, DataBaseCorrupt {
        File ctFile = dataBasePath.toFile();
        
        //get all directories name
        String[] dirList = ctFile.list();
        for (String curTable : dirList) {
            Path curDir = ctFile.toPath().resolve(curTable);
            if (!Files.isDirectory(curDir)) {
                throw new DataBaseCorrupt("Directory contains non-directory files");
            }
            int c = setTableRowCount(curDir.toString());
            tableRowCount.put(curTable, c);
         }
    }
    
    private int setTableRowCount(String curTable) throws MapException, DataBaseCorrupt {
        File ctFile = new File(curTable);
        String[] dirList = ctFile.list();
        int c = 0;
        for (String curName : dirList) {
            Path curDir = ctFile.toPath().resolve(curName);
            if (curName.matches("([0-9]|1[0-5])\\.dir")) {
                c += tableRowCount(curDir);
            } else {
                throw new DataBaseCorrupt("DataBase corrupted: incorrect directoryName: " + curName);
            }
        }
        return c;
    }
    
    private int tableRowCount(Path tableDir) throws DataBaseCorrupt, MapException {
        File curDir = tableDir.toFile();
        String[] dirList = curDir.list();
        int rowCount = 0;
        for (String curName : dirList) {
            Path curFile = tableDir.resolve(curName);
            if (curName.matches("([0-9]|1[0-5])\\.dat")) {
                Table it = new Table(curFile.toString());
                int curCnt = it.getRowCount();
                if (curCnt == 0) {
                    throw new DataBaseCorrupt("DataBase corrupted: dataBase contains empty files"); 
                } else {
                    rowCount += curCnt;
                }
            } else {
                throw new DataBaseCorrupt("DataBase corrupted: incorrect fileName");
            }
        }
        
        return rowCount;
    }
    
    public void showTables() {
        Set<String> it = tableRowCount.keySet();   
        for (String cur : it) {
            System.out.println(cur + " " + tableRowCount.get(cur));
        }
    }
    
    public void create(String tbName) throws MapException, IOException {
        if (tableRowCount.containsKey(tbName)) {
            throw new MapException(tbName + ":  tablename exists");
        }
        Files.createDirectory(dataBasePath.resolve(tbName));
        tableRowCount.put(tbName, 0);
    }
    
    public void drop(String tbName) throws MapException, IOException {
        if (!tableRowCount.containsKey(tbName)) {
            throw new MapException(tbName + ":  no table");
        }
        if (isChoose && sTable.getTableName().equals(tbName)) {
            isChoose = false;
            sTable = null;
        }
        Functions.rmDir(dataBasePath.resolve(tbName));
        tableRowCount.remove(tbName);
        System.out.println("dropped");
    }
    
    public Path getPath() {
        return dataBasePath;
    }
    
    public void use(String tbName) throws TableConnectionException, DataBaseCorrupt, MapException, IOException {
        if (!tableRowCount.containsKey(tbName)) {
            throw new MapException("use: '" + tbName + "': tablename not exists");
        }
        if (isChoose) {
            // 
            this.commit();
        }
        sTable = new SuperTable(dataBasePath.resolve(tbName));
        isChoose = true;
        System.out.println("Using " + tbName);
    }
    
    public void list() throws MapException {
        if (!isChoose) {
            throw new MapException("no table");
        } else {
            sTable.showTableList();
            System.out.println("");
        }
        
    }
    
    public void commit() throws MapException, IOException {
        if (isChoose) {
            sTable.commit();
        }
    }
    
    public void put(String key, String value) throws MapException, IOException, DataBaseCorrupt {
        if (!isChoose) {
            throw new MapException("no table");
        } else {
            if (!sTable.put(key, value)) {
                tableRowCount.put(sTable.getTableName(), tableRowCount.get(sTable.getTableName()) + 1);
            }
        }
    }
    
    public void get(String key) throws MapException {
        if (!isChoose) {
            throw new MapException("no table");
        } else {
            sTable.get(key);
        }
    }
    
    public void remove(String key) throws MapException {
        if (!isChoose) {
            throw new MapException("no table");
        } else {
            if (sTable.remove(key)) {
                int k = tableRowCount.get(sTable.getTableName()) - 1;
                tableRowCount.put(sTable.getTableName(), k);
            }
        }
    }

}
