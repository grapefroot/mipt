package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import strings.Table;

public class MyTable implements Table {
    File tableRoot;
    HashMap<String, String> data;

    public MyTable(File tableFile) {
        data = FolderData.loadDb(tableFile);
        tableRoot = tableFile;
    }

    @Override
    public String getName() {
        return tableRoot.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            System.out.println("found\n" + data.get(key));
            return data.get(key);
        } else {
            System.out.println("not found");
            return null;
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            System.out.println("overwrite\nold value");
            String oldValue = data.get(key);
            data.put(key, value);
            return oldValue;
        } else {
            System.out.println("new");
            data.put(key, value);
            return null;
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Bad key value");
        }

        if (data.containsKey(key)) {
            System.out.println("removed");
            String oldValue = data.get(key);
            data.remove(key);
            return oldValue;
        } else {
            System.out.println("not found");
            return null;
        }
    }

    @Override
    public int size() {
        List<String> keyList = new ArrayList<String>();
        for (String key : data.keySet()) {
            keyList.add(key);
        }
        System.out.println(keyList.size());
        return keyList.size();
    }
    
    @Override
    public int commit() {
        int diffSize = diff();
        FolderData.saveDb(data, tableRoot);
        System.out.println(diffSize);
        return diffSize;
    }

    @Override
    public int rollback() {
        int diffSize = diff();
        data = FolderData.loadDb(tableRoot);
        System.out.println(diffSize);
        return diffSize;
    }

    public int diff() {
        HashMap<String, String> oldData = FolderData.loadDb(tableRoot);
        Set<String> allKeys = new HashSet<String>(oldData.keySet());
        allKeys.addAll(data.keySet());
        
        int size = 0;

        for (String k : allKeys) {
            if ((data.containsKey(k) && !oldData.containsKey(k))
                    || (!data.containsKey(k) && oldData.containsKey(k))
                        || (!data.get(k).equals(oldData.get(k)))) {
                System.out.println(" " + data.get(k) + " " + oldData.get(k));
                size++;
            }
        }
        return size;
    }

    @Override
    public List<String> list() {
        List<String> keyList = new ArrayList<String>();
        for (String key : data.keySet()) {
            System.out.println(key);
            keyList.add(key);
        }
        return keyList;
    }
}
