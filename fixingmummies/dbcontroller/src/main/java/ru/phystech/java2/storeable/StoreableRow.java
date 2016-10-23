package ru.phystech.java2.storeable;

import org.springframework.stereotype.Service;
import ru.phystech.java2.structured.ColumnFormatException;
import ru.phystech.java2.structured.Storeable;
import ru.phystech.java2.structured.Table;
import ru.phystech.java2.utils.StringCreationTools;

import java.util.ArrayList;
import java.util.List;

public class StoreableRow implements Storeable {
    private List<Class<?>> types;
    private List<Object> row;

    public StoreableRow(Table givenTable) {
        types = new ArrayList<>();
        row = new ArrayList<>();
        for (int i = 0; i < givenTable.getColumnsCount(); ++i) {
            types.add(givenTable.getColumnType(i));
            row.add(null);
        }
    }

    public StoreableRow(Table givenTable, List<?> givenValues)
            throws ColumnFormatException, IndexOutOfBoundsException {
        types = new ArrayList<>();
        row = new ArrayList<>();
        if (givenTable.getColumnsCount() != givenValues.size()) {
            throw new IndexOutOfBoundsException("ru.phystech.java2.storeable row: "
                    + "size of value-list not equals to amount of columns in table");
        }

        for (int i = 0; i < givenValues.size(); ++i) {
            if (givenValues.get(i) != null) {
                if (givenTable.getColumnType(i) != givenValues.get(i).getClass()) {
                    throw new ColumnFormatException("ru.phystech.java2.storeable row: "
                            + "types in value-list and in table's columns conflict");
                }
            }
            types.add(givenTable.getColumnType(i));
            row.add(givenValues.get(i));
        }
    }

    @Override
    public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
        int columnsCount = types.size();
        if (columnIndex < 0 || columnIndex > columnsCount - 1) {
            throw new IndexOutOfBoundsException("set column at: bad index");
        }
        if (value != null) {
            if (value.getClass() != types.get(columnIndex)) {
                throw new ColumnFormatException("set column at: bad type");
            }
        }
        row.set(columnIndex, value);
    }

    @Override
    public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
        int columnsCount = types.size();
        if (columnIndex < 0 || columnIndex > columnsCount - 1) {
            throw new IndexOutOfBoundsException("get column at: bad index");
        }
        return row.get(columnIndex);
    }

    public Object getSomethingAt(int columnIndex, Class<?> valueClass)
            throws ColumnFormatException, IndexOutOfBoundsException {
        int columnsCount = types.size();
        if (columnIndex < 0 || columnIndex > columnsCount - 1) {
            throw new IndexOutOfBoundsException("get something at: bad index");
        }
        if (valueClass != types.get(columnIndex)) {
            throw new ColumnFormatException("get something at: bad type");
        }
        return row.get(columnIndex);
    }

    @Override
    public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Integer) getSomethingAt(columnIndex, Integer.class);
    }

    @Override
    public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Long) getSomethingAt(columnIndex, Long.class);
    }

    @Override
    public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Byte) getSomethingAt(columnIndex, Byte.class);
    }

    @Override
    public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Float) getSomethingAt(columnIndex, Float.class);
    }

    @Override
    public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Double) getSomethingAt(columnIndex, Double.class);
    }

    @Override
    public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (Boolean) getSomethingAt(columnIndex, Boolean.class);
    }

    @Override
    public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
        return (String) getSomethingAt(columnIndex, String.class);
    }

    @Override
    public String toString() {
        return StringCreationTools.createString(getClass().getSimpleName(), row);
    }
}
