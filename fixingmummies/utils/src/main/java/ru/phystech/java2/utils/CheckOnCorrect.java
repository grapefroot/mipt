package ru.phystech.java2.utils;

import ru.phystech.java2.structured.Storeable;
import ru.phystech.java2.utils.storage.ColumnFormatException;

import java.util.List;

public class CheckOnCorrect {
    public static boolean goodName(String givenName) {
        if (givenName == null) {
            return false;
        }
        return givenName.matches("[a-zA-Zа-яА-Я0-9]+");
    }

    public static boolean goodArg(String arg) {
        if (arg == null) {
            return false;
        }
        if (arg.trim().isEmpty()) {
            return false;
        }
        return !arg.matches(".*\\s+.*");
    }

    public static boolean goodColumnTypes(List<Class<?>> givenTypes) {
        if (givenTypes == null || givenTypes.size() == 0) {
            return false;
        }
        for (Class<?> type : givenTypes) {
            if (type == Integer.class
                    || type == Long.class
                    || type == Byte.class
                    || type == Float.class
                    || type == Double.class
                    || type == Boolean.class
                    || type == String.class) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    private static Object getValueWithType(Storeable storeable, int columnIndex,
                                           Class<?> columnType) throws ColumnFormatException {
        switch (columnType.getName()) {
            case "java.lang.Integer":
                return storeable.getIntAt(columnIndex);
            case "java.lang.Long":
                return storeable.getLongAt(columnIndex);
            case "java.lang.Byte":
                return storeable.getByteAt(columnIndex);
            case "java.lang.Float":
                return storeable.getFloatAt(columnIndex);
            case "java.lang.Double":
                return storeable.getDoubleAt(columnIndex);
            case "java.lang.Boolean":
                return storeable.getBooleanAt(columnIndex);
            case "java.lang.String":
                return storeable.getStringAt(columnIndex);
            default:
                throw new ColumnFormatException("column has a wrong type");
        }
    }

    public static boolean goodStoreable(Storeable value, List<Class<?>> columnTypes) {
        if (value == null) {
            return false;
        }
        int columnIndex = 0;
        try {
            for (Class<?> columnType : columnTypes) {
                getValueWithType(value, columnIndex, columnType);
                columnIndex++;
            }
            try {
                value.getColumnAt(columnIndex);
                return false;
            } catch (IndexOutOfBoundsException exc) {
                return true;
            }
        } catch (IndexOutOfBoundsException | ColumnFormatException exc) {
            return false;
        }
    }
}
