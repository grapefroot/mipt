package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.table;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.TableRowSerializer;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.commands.Command;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 07.10.2014.
 */
public class CreateTableCommand extends TableCommand implements Command {


    public CreateTableCommand(DataBaseTableProvider shell) {
        super(shell);
    }

    @Override
    public String executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 2) {
            throw new Exception("usage create tablename [JSON types]");
        }

        try {
            List<Class<?>> types = new ArrayList<>();
            String formatJSON = arguments.get(1);

            formatJSON = formatJSON.trim();
            if (formatJSON.length() < 3 || formatJSON.charAt(0) != '['
                    || formatJSON.charAt(formatJSON.length() - 1) != ']'
                    || formatJSON.charAt(formatJSON.length() - 2) == ',') {
                throw new ParseException("invalid JSON format", 0);
            }
            List<Class<?>> values = new ArrayList<>();
            String[] tokens = formatJSON.substring(1, formatJSON.length() - 1).split(",");
            for (String string: tokens) {
                if (string.isEmpty()) {
                    throw new ParseException("empty object in JSONArray", 0);
                }
            }

            for (String token: tokens) {
                Class<?> currentClass = TableRowSerializer.stringToClass(token.trim());

                if (currentClass == null) {
                    throw new ParseException("invalid types in JSONArray", 0);
                }

                types.add(currentClass);
            }

            Table table = shell.createTable(arguments.get(0), types);
            if (table == null) {
                return arguments.get(0) + " exists";
            }
            return "created";

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    @Override
    public String getName() {
        return "create";
    }
}
