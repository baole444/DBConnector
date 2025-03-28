package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.models.enums.Collection;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;
import org.bson.Document;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle delete query parsing using reflection.
 */
public class DeleteParser {
    private final SqlDBQuery SQLdBQuery;
    private final MongoDBQuery MongoDBQuery;

    /**
     * Constructor of {@link DeleteParser}.
     * For NoSQL query, see {@link #DeleteParser(MongoDBQuery)}
     * @param SQLdBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public DeleteParser(SqlDBQuery SQLdBQuery) {
        this.SQLdBQuery = SQLdBQuery;
        this.MongoDBQuery = null;
    }

    /**
     * Constructor of {@link DeleteParser}.
     * For SQL query, see {@link #DeleteParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public DeleteParser(MongoDBQuery mongoDBQuery) {
        this.MongoDBQuery = mongoDBQuery;
        this.SQLdBQuery = null;
    }

    public <T> int delete(T model, String condition, Object... params) throws IllegalAccessException, SQLException {
        if (MongoDBQuery == null) {
            return deleteSQL(model, condition, params);
        } else if (SQLdBQuery == null) {
            return deleteMongo(model, condition, params);
        } else {
            return -1;
        }
    }

    /**
     * A method invokes {@link SqlDBQuery#setDataSQL(String, Object...)}
     * to delete data based on {@code Object} model's primary key attribute.
     * @param model an instance of a data model extending {@link dbConnect.DataModel}.
     * @param condition a set of conditions used for the query.
     * @param params parameters of the conditions in order.
     * @return number of deleted entries.
     * @param <T> a data model class extending {@link dbConnect.DataModel}
     * @throws IllegalAccessException when missing {@link DataModel#getTable()} method from the class.
     * @throws IllegalArgumentException when missing an attribute marked with {@link PrimaryField} annotation or that attribute's value is missing.
     * @throws SQLException when there is an error occurred during data deletion.
     */
    public <T> int deleteSQL(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException, SQLException {
        Class<?> modelClass = model.getClass();

        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getTable() method that return a Table enum.");
        }

        String query;
        if (condition != null && !condition.isBlank()) {
            query = "delete from" + table.getName() + " where " + condition;

            assert SQLdBQuery != null;
            return SQLdBQuery.setDataSQL(query, params);
        } else {
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

            Object primaryKeyValue = getPrimaryKeyValue(model, modelClass);

            query = "delete from " + table.getName() + " where " + primaryField.getName() + " = ?";

            assert SQLdBQuery != null;
            return SQLdBQuery.setDataSQL(query, primaryKeyValue);
        }
    }

    public <T> int deleteMongo(T model, String condition, Object... params) throws IllegalAccessException {
        Class<?> modelClass = model.getClass();

        Collection collection;

        try {
            collection = (Collection) modelClass.getMethod("getCollection").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getCollection() method that return a Table enum.");
        }

        Document filter;

        if (condition != null && !condition.isBlank()) {
            filter = parseCondition(condition, params);
        } else {
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

            Object primaryKeyValue = getPrimaryKeyValue(model, modelClass);

            if (primaryKeyValue == null) {
                throw new IllegalArgumentException("Missing value for primary key!");
            }

            filter = new Document(primaryField.getName(),primaryKeyValue);


        }
        assert MongoDBQuery != null;
        return MongoDBQuery.setMongoData(collection.getName()).delete(filter).count();
    }

    private static <T> Object getPrimaryKeyValue(T model, Class<?> modelClass) throws IllegalAccessException {
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
        return primaryField.get(model);
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
