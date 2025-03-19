package dbConnect.execution;

import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * Handle delete query parsing using reflection.
 */
public class DeleteParser {
    private final SqlDBQuery SQLdBQuery;

    /**
     * Constructor of {@link DeleteParser}.
     * @param SQLdBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public DeleteParser(SqlDBQuery SQLdBQuery) {
        this.SQLdBQuery = SQLdBQuery;
    }

    /**
     * A method invokes {@link SqlDBQuery#setDataSQL(String, Object...)}
     * to delete data based on {@code Object} model's primary key attribute.
     * @param model an instance of a Data Model. It must contain a method call {@code getTable()}.<br>
     *             It must have an attribute marked with {@link PrimaryField} annotation.
     * @return the count of deleted rows.
     * @param <T> Object
     * @throws IllegalAccessException when missing {@code getTable()} method from the data model.
     * @throws IllegalArgumentException when missing an attribute marked with {@link PrimaryField} annotation or that attribute's value is missing.
     * @throws SQLException when there is an error occurred during data deletion.
     */
    public <T> int delete(T model) throws IllegalAccessException, SQLException {
        Class<?> modelClass = model.getClass();

        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTable() method that return a Table enum.");
        }

        // Find primary key field
        Field primaryField = null;
        for (Field field : modelClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryField.class)) {
                primaryField = field;
                break;
            }
        }

        if (primaryField == null) {
            throw new IllegalArgumentException("Model is missing a primary field!");
        }

        // Get primary key value
        primaryField.setAccessible(true);
        Object primaryKeyValue = primaryField.get(model);

        if (primaryKeyValue == null) {
            throw new IllegalArgumentException("Missing value for primary key!");
        }

        String query = "delete from " + table.getName() + " where " + primaryField.getName() + " = ?";

        return SQLdBQuery.setDataSQL(query, primaryKeyValue);
    }
}
