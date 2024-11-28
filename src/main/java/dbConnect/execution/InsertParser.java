package dbConnect.execution;

import dbConnect.DBQuery;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;

import java.lang.annotation.Annotation;
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

            Object fieldValue = getFieldValue(model, field);

            columns.append(field.getName()).append(", ");
            placeholders.append("?, ");
            val.add(fieldValue);
        }

        // Trim command and space at the end
        if (columns.length() > 0) {
            columns.setLength(columns.length() - 2);
            placeholders.setLength(placeholders.length() -2);
        }


        String query = "insert into " + table.getName() + " (" + columns + ") values (" + placeholders + ")";

        return dbQuery.setData(query, val.toArray());
    }

    /**
     * Internal method to get the value of a field.
     * @param model an instance of a Data Model.
     * @param field an attribute extracted from a model.
     * @return value of the field as an {@code object}.
     * @param <T> Object.
     * @throws IllegalAccessException when failed to extract field's details.
     */
    private static <T> Object getFieldValue(T model, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(model);
        // Verify value for not null field
        if (field.isAnnotationPresent(NotNullField.class)) {
            if (fieldValue == null) {
                throw new IllegalArgumentException("Missing value for field: " + field.getName() + " with not null annotation");
            }
        }

        // Trim string that passes annotation's limit
        if (field.isAnnotationPresent(MaxLength.class)) {
            if (fieldValue instanceof String) {
                int maxLength = field.getAnnotation(MaxLength.class).value();
                String newTrim = ((String) fieldValue).length() > maxLength ?
                        ((String) fieldValue).substring(0, maxLength) : (String) fieldValue;

                fieldValue = newTrim;
            } else {
                throw new IllegalArgumentException("Field: " + field.getName() + " with max length annotation is not a String!");
            }
        }
        return fieldValue;
    }

}
