package dbConnect.execution;

import dbConnect.Utility;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.enums.Collection;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;
import org.bson.Document;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle update query parsing using reflection.
 */
public class UpdateParser {
    private final SqlDBQuery SQLdBQuery;
    private final MongoDBQuery MongoDBQuery;

    /**
     * Constructor of {@link UpdateParser}.
     * For NoSQL query, see {@link #UpdateParser(MongoDBQuery)}
     * @param SQLdBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public UpdateParser(SqlDBQuery SQLdBQuery) {
        this.SQLdBQuery = SQLdBQuery;
        this.MongoDBQuery = null;
    }

    /**
     * Constructor of {@link UpdateParser}.
     * For SQL query, see {@link #UpdateParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public UpdateParser(MongoDBQuery mongoDBQuery) {
        this.MongoDBQuery = mongoDBQuery;
        this.SQLdBQuery = null;
    }

    public <T> int update(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException, SQLException {
        if (MongoDBQuery == null) {
            return updateSQL(model, condition, params);
        } else if (SQLdBQuery == null) {
            return updateMongo(model, condition, params);
        } else {
            return -1;
        }
    }

    /**
     * A method invokes {@link SqlDBQuery#setDataSQL(String, Object...)}
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
    public <T> int updateSQL(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException, SQLException {
        Class<?> modelClass = model.getClass();

        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getTable() method that return a Table enum.");
        }

        // Prepare primary key, value to update and the condition

        List<Object> val = new ArrayList<>();
        StringBuilder setTerm = new StringBuilder();
        Field primaryField = null;
        Object primaryKeyValue = null;

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

        if (setTerm.isEmpty()) {
            throw new IllegalArgumentException("No target field for updating specified.");
        }

        setTerm.setLength(setTerm.length() - 2); // remove comma and trailing space at the end

        String query;

        if (condition != null  && !condition.isBlank()) {
            query = "update " + table.getName() + " set " + setTerm + " where " + condition;
            val.addAll(List.of(params));
        } else {
            if (primaryField == null || primaryKeyValue == null) {
                throw new IllegalAccessException("Missing value for primary key or the key field itself!");
            }


            query = "update " + table.getName() + " set " + setTerm + " where " + primaryField.getName() + " = ?";

            val.add(primaryKeyValue);
        }

        assert SQLdBQuery != null;
        return  SQLdBQuery.setDataSQL(query, val.toArray());
    }

    public <T> int updateMongo(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException {
        Class<?> modelClass =model.getClass();

        Collection collection;

        try {
            collection = (Collection) modelClass.getMethod("getCollection").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getCollection() method that return a Table enum.");
        }

        Document filter = new Document();

        if (condition != null && !condition.isBlank()) {
            int filterArgCount = Utility.countFilterParams(condition);

            if (params.length < filterArgCount) {
                throw new IllegalArgumentException("Not enough filter parameters for declared filter argument");
            }

            filter = Document.parse(Utility.appendPlaceholderValue(condition, params, filterArgCount));
        }

        Document updateFields = new Document();
        Field primaryField = null;
        Object primaryKeyValue = null;

        Field[] fields = modelClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(PrimaryField.class)) {
                primaryField = field;
                primaryKeyValue = field.get(model);
                continue;
            }

            Object fieldValue = getFieldValue(model, field);
            if (fieldValue != null) {
                updateFields.append(field.getName(), fieldValue);
            }
        }

        if (updateFields.isEmpty()) {
            throw new IllegalArgumentException("No target field for updating specified.");
        }

        if (condition == null || condition.isBlank()) {
            if (primaryField == null || primaryKeyValue == null) {
                throw new IllegalAccessException("Missing value for primary key or the key field itself!");
            }

            filter.append(primaryField.getName(), primaryKeyValue);
        }

        assert MongoDBQuery != null;
        return MongoDBQuery.setMongoData(collection.getName()).update(filter, new Document("$set", updateFields)).count();
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

                if (((String) fieldValue).length() > maxLength) {
                    fieldValue = ((String) fieldValue).substring(0, maxLength);
                }

            } else {
                throw new IllegalArgumentException("Field: " + field.getName() + " with max length annotation is not a String!");
            }
        }
        return fieldValue;
    }

    private Document parseCondition(String condition, Object... params) {
        Document filter = new Document();

        List<String> operators = Arrays.asList("=", ">", "<", ">=", "<=", "!=", "LIKE");

        Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\\s*(=|>|<|>=|<=|!=|LIKE)\\s*\\?");
        Matcher matcher = pattern.matcher(condition);

        int paramIndex = 0;

        while (matcher.find() && paramIndex < params.length) {
            String field = matcher.group(1);
            String operator = matcher.group(2);
            Object value = params[paramIndex++];

            switch (operator) {
                case "=" -> filter.append(field, value);
                case "!=" -> filter.append(field, new Document("$ne", value));
                case ">" -> filter.append(field, new Document("$gt", value));
                case "<" -> filter.append(field, new Document("$lt", value));
                case ">=" -> filter.append(field, new Document("$gte", value));
                case "<=" -> filter.append(field, new Document("$lte", value));
                case "LIKE" -> filter.append(field, new Document("$regex", value.toString()).append("$options", "i"));
                default -> throw new IllegalArgumentException("Unknown operator syntax: " + operators);
            }
        }

        return filter;
    }

}
