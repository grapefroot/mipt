package ru.phystech.java2.table;

import java.io.IOException;
import java.util.List;


public interface Command {
    String getName();

    int getAmArgs();

    void work(List<String> args) throws IOException;
}
