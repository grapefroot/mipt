package ru.fizteh.fivt.students.gudkov394.Storable.src;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kagudkov on 07.11.14.
 */
public class MySerialize implements Serializable {

    interface Read {
        Object getObject(String string) throws ParseException;
    }

    interface Write {
        String getString(Object object);
    }

    private Map<Class, Read> readMap = new HashMap<>();
    private Map<Class, Write> writeMap = new HashMap<>();

    public MySerialize() {
        readMap.put(Integer.class, (String s) -> Integer.valueOf(s));
        readMap.put(Long.class, (String s) -> Long.valueOf(s));
        readMap.put(Float.class, (String s) -> Float.valueOf(s));
        readMap.put(Double.class, (String s) -> Double.valueOf(s));
        readMap.put(Byte.class, (String s) -> Byte.valueOf(s));
        readMap.put(Boolean.class, (String s) -> {
            if (s.trim().toLowerCase().equals("true")) {
                return true;
            } else if (s.trim().toLowerCase().equals("false")) {
                return false;
            }
            throw new ParseException("not valid boolean value", 0);
        });
        readMap.put(String.class, (String s) -> {
            if (s.length() > 1 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                return s.substring(1, s.length() - 1);
            }
            throw new ParseException("not valid String value", 0);
        });
        writeMap.put(Integer.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        writeMap.put(Long.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        writeMap.put(Float.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        writeMap.put(Double.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        writeMap.put(Byte.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        writeMap.put(Boolean.class, new Write() {
            @Override
            public String getString(Object object) {
                return object.toString();
            }
        });

        writeMap.put(String.class, new Write() {
            @Override
            public String getString(Object object) {
                return "\"" + object + "\"";
            }
        });
    }

    public Storeable deserialize(Table table, String value) throws ParseException {
        value = value.trim();
        if (value.length() < 3 || value.charAt(0) != '[' || value.charAt(value.length() - 1) != ']'
                || value.charAt(1) == ',' || value.charAt(value.length() - 2) == ',') {
            throw new ParseException("JSON is invalid, failed with '[' or ','", 0);
        }
        String[] tokens = value.substring(1, value.length() - 1).split(",");
        if (tokens.length != table.getColumnsCount()) {
            throw new ParseException("wrong number of column", 0);
        }
        List<Object> contents = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); i++) {
            try {
                if (!("null".equals(tokens[i].trim().toLowerCase()))) {
                    contents.add(readMap.get(table.getColumnType(i)).getObject(tokens[i].trim()));
                } else {
                    contents.add(null);
                }
            } catch (NumberFormatException e) {
                throw new ParseException("not a valid ", 0);
            }
        }
        return new TableContents(contents);
    }

    public String serialize(Table table, Storeable value) {
        int columnsCount = table.getColumnsCount();
        StringBuilder stringBuilder = new StringBuilder("[");
        for (int i = 0; i < columnsCount; i++) {
            if (value != null) {
                if (value.getColumnAt(i) != null) {
                    stringBuilder.append(writeMap.get(table.getColumnType(i)).getString(value.getColumnAt(i)));
                } else {
                    stringBuilder.append("null");
                }
            }
            stringBuilder.append(',');
        }
        stringBuilder.setCharAt(stringBuilder.length() - 1, ']');
        return stringBuilder.toString();
    }
}
