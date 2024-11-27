package dbConnect.execution;

import dbConnect.DBQuery;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle insert query parsing using reflection.
 */
public class InsertParser {
    private final DBQuery dbQuery;

    /**
     * Constructor of {@link InsertParser}.
     * @param dbQuery an instance of {@link DBQuery#DBQuery(String, String, String)}
     */
    public InsertParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

    /**
     * A method invokes {@link DBQuery#setData(String, Object...)}
     * to insert data from an {@code Object} model.
     * @param model an instance of a Data Model. It must contain a method call {@code getTable()}.
     * @return the count of inserted rows.
     * @param <T> Object
     * @throws IllegalAccessException when missing {@code getTable()} method from the data model.
     * @throws SQLException when there is an error occurred during data insertion.
     */
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

            // Verify value for not null field
            if (field.isAnnotationPresent(NotNullField.class)) {
                Object fieldVal = field.get(model);
                if (fieldVal == null) {
                    throw new IllegalArgumentException("Missing value for field: " + field.getName() + "with not null annotation");
                }
            }

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
