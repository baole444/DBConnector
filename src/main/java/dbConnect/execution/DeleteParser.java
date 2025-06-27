package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.Utility;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.PrimaryField;
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

    /**
     * A method to determine the inner delete method.
     * @param model an instance of a Data Model
     * @param condition a set of conditions used for the query
     * @param params parameters of the conditions in order.
     * @return number of deleted entries.
     * @param <T> a data model class extending {@link dbConnect.DataModel}
     * @throws IllegalAccessException when mismatch between scope and inner method call happened.
     * @throws SQLException occurred when SQL error happened.
     */
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
     * @throws IllegalAccessException calling this method without SQL scope.
     * @throws IllegalArgumentException when missing an attribute marked with {@link PrimaryField} annotation or that attribute's value is missing.
     * @throws SQLException when there is an error occurred during data deletion.
     */
    private <T> int deleteSQL(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException, SQLException {
        if (sqlDBQuery == null) throw new IllegalAccessException("Calling an SQL method without an SQL scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        String tableName = ((DataModel<?>) model).getTableName();
        String query;

        if (condition != null && !condition.isBlank()) {
            query = "delete from" + tableName + " where " + condition;
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

            query = "delete from " + tableName + " where " + primaryField.getName() + " = ?";
        }

        return sqlDBQuery.setDataSQL(query, params);
    }

    private <T> int deleteMongo(T model, String condition, Object... params) throws IllegalAccessException {
        if (mongoDBQuery == null) throw new IllegalAccessException("Calling a MongoDB method without a MongoDB scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        String collectionName = ((DataModel<?>) model).getCollectionName();
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

        return mongoDBQuery.setMongoData(collectionName).delete(filter).count();
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
