package dbConnect.execution;

import dbConnect.DBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class DeleteParser {
    private final DBQuery dbQuery;

    public DeleteParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

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

        return dbQuery.setData(query, primaryKeyValue);
    }
}
