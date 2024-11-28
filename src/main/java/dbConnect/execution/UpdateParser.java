package dbConnect.execution;

import dbConnect.DBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle update query parsing using reflection.
 */
public class UpdateParser {
    private final DBQuery dbQuery;

    /**
     * Constructor of {@link UpdateParser}.
     * @param dbQuery an instance of {@link DBQuery#DBQuery(String, String, String)}
     */
    public UpdateParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

    /**
     * A method invokes {@link DBQuery#setData(String, Object...)}
     * to update data from an {@code Object} model base on it's {@link PrimaryField} to the database server.
     * @param model an instance of a Data Model. It must contain a method call {@code getTable()}.<br>
     *              It must have an attribute marked with {@link PrimaryField} annotation.
     * @return the count of updated rows.
     * @param <T> Object
     * @throws IllegalAccessException when missing {@code getTable()} method from the data model.<br>
     *                               When missing an attribute marked with {@link PrimaryField} annotation or that attribute's value is missing.
     * @throws IllegalArgumentException when a field marked with {@link NotNullField} is missing its value.
     * @throws SQLException when there is an error occurred during data update.
     */
    public <T> int update(T model) throws IllegalAccessException, SQLException {
        Class<?> modelClass = model.getClass();

        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTable() method that return a Table enum.");
        }

        // Prepare primary key, value to update and the condition
        Field primaryField = null;
        Object primaryKeyValue = null;
        List<Object> val = new ArrayList<>();
        StringBuilder setTerm = new StringBuilder();

        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            // Find primary field
            if (field.isAnnotationPresent(PrimaryField.class)) {
                primaryField = field;
                primaryKeyValue = field.get(model);
                continue;
            }

            Object fieldValue = getFieldValue(model, field);

            if (fieldValue != null) {
                setTerm.append(field.getName()).append(" = ?, ");
                val.add(fieldValue);
            }
        }

        if (primaryField == null || primaryKeyValue == null) {
            throw new IllegalAccessException("Missing value for primary key or the key field itself!");
        }

        setTerm.setLength(setTerm.length() - 2);
        String query = "update " + table.getName() + " set " + setTerm + " where " + primaryField.getName() + " = ?";

        val.add(primaryKeyValue);

        return  dbQuery.setData(query, val.toArray());
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

        if (field.isAnnotationPresent(NotNullField.class) && fieldValue == null) {
            throw new IllegalArgumentException("Missing value for field: " + field.getName() + " with not null annotation");
        }

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
