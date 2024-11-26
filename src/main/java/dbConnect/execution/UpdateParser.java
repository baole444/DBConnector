package dbConnect.execution;

import dbConnect.DBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateParser {
    private final DBQuery dbQuery;

    public UpdateParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

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

            Object fieldValue = field.get(model);
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
}
