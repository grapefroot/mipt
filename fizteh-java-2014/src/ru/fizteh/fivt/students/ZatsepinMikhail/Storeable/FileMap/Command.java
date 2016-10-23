package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap;

public abstract class Command<T> {
    protected String name;
    protected int numberOfArguments;

    public abstract boolean run(T object, String[] args);

    @Override
    public final String toString() {
        return name;
    }
}
