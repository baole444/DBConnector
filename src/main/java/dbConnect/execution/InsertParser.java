package dbConnect.execution;

import dbConnect.DBQuery;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.enums.Table;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsertParser {
    private final DBQuery dbQuery;

    public InsertParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

    public <T> int insert(T model) throws IllegalAccessException, SQLException {
        Class<?> modelClass = model.getClass();

        // Get table name using reflection
        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTable() method that return a Table enum.");
        }


        // Building SQL command
        Field[] fields = modelClass.getDeclaredFields();
        List<Object> val = new ArrayList<>();
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);

            // Ignore auto generated fields
            if (field.isAnnotationPresent(AutomaticField.class)) { continue; }

            columns.append(field.getName()).append(", ");
            placeholders.append("?, ");
            val.add(field.get(model));
        }

        // Trim command and space at the end
        if (columns.length() > 0) {
            columns.setLength(columns.length() - 2);
            placeholders.setLength(placeholders.length() -2);
        }


        String query = "insert into " + table.getName() + " (" + columns + ") values (" + placeholders + ")";

        return dbQuery.setData(query, val.toArray());
    }
    
}
