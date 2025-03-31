package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.Utility;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.enums.Collection;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.sql.SQLException;

/**
 * Handle delete query parsing using reflection.
 */
public class DeleteParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;

    /**
     * Constructor of {@link DeleteParser}.
     * For NoSQL query, see {@link #DeleteParser(MongoDBQuery)}
     * @param sqlDBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public DeleteParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
    }

    /**
     * Constructor of {@link DeleteParser}.
     * For SQL query, see {@link #DeleteParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public DeleteParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
    }

    public <T> int delete(T model, String condition, Object... params) throws IllegalAccessException, SQLException {
        if (mongoDBQuery == null) {
            return deleteSQL(model, condition, params);
        } else if (sqlDBQuery == null) {
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
        if (sqlDBQuery == null) throw new IllegalAccessException("Calling an SQL method without an SQL scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getTable() method that return a Table enum.");
        }

        String query;

        if (condition != null && !condition.isBlank()) {
            query = "delete from" + table.getName() + " where " + condition;
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

            params[0] = getPrimaryKeyValue(model, modelClass);

            query = "delete from " + table.getName() + " where " + primaryField.getName() + " = ?";
        }

        return sqlDBQuery.setDataSQL(query, params);
    }

    public <T> int deleteMongo(T model, String condition, Object... params) throws IllegalAccessException {
        if (mongoDBQuery == null) throw new IllegalAccessException("Calling a MongoDB method without a MongoDB scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        Collection collection;

        try {
            collection = (Collection) modelClass.getMethod("getCollection").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getCollection() method that return a Table enum.");
        }

        Document filter;

        if (condition != null && !condition.isBlank()) {
            int filterArgCount = Utility.countFilterParams(condition);

            if (params.length < filterArgCount) {
                throw new IllegalArgumentException("Not enough filter parameters for declared filter argument");
            }

            filter = Document.parse(Utility.appendPlaceholderValue(condition, params, filterArgCount));
        } else {
            Field _idField = null;
            for (Field field : modelClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(MongoOnly.class) && field.getName().equals("_id")) {
                    _idField = field;
                    break;
                }
            }

            if (_idField == null) {
                throw new IllegalArgumentException("Model is missing an _id field!");
            }

            ObjectId idKeyValue = (ObjectId) _idField.get(model);

            if (idKeyValue == null) {
                throw new IllegalArgumentException("Missing value for _id key!");
            }

            filter = new Document(_idField.getName(), idKeyValue);
        }

        return mongoDBQuery.setMongoData(collection.getName()).delete(filter).count();
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
}
