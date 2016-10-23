package ru.phystech.java2.utils;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<String> splitByDelimiter(String cmd, String delimiter) {
        cmd.trim();
        String[] tokens = cmd.split(delimiter);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("") && !tokens[i].matches("\\s+")) {
                result.add(tokens[i]);
            }
        }
        return result;
    }
}
