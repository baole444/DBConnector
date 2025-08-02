package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.Utility;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.constrain.MySQLOnly;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.notnull.NotNullField;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle update query parsing using reflection.
 */
public class UpdateParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;
    private final FieldReflector fieldReflector;

    /**
     * Constructor of {@link UpdateParser}.
     * For NoSQL query, see {@link #UpdateParser(MongoDBQuery)}
     * @param sqlDBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public UpdateParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
        this.fieldReflector = new FieldReflector(sqlDBQuery);
    }

    /**
     * Constructor of {@link UpdateParser}.
     * For SQL query, see {@link #UpdateParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public UpdateParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
        this.fieldReflector = new FieldReflector(mongoDBQuery);
    }

    /**
     * A method to determine the correct inner update method.
     * @param model an instance of a Data Model.
     * @param condition a strings of condition to perform update on.
     * @param params value of each condition in order.
     * @return the count of updated rows.
     * @param <T> type of the data model to update.
     * @throws IllegalAccessException data model class is missing required method.
     * @throws IllegalArgumentException failed to find update value.
     * @throws SQLException error while performing MySQL query.
     */
    public <T> int update(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException, SQLException {
        if (mongoDBQuery == null) {
            return updateSQL(model, condition, params);
        } else if (sqlDBQuery == null) {
            return updateMongo(model, condition, params);
        } else {
            return -1;
        }
    }

    /**
     * A method invokes {@link SqlDBQuery#setDataSQL(String, Object...)}
     * to update data from an {@code Object} model base on conditions,
     * or it's {@link PrimaryField} to the database server.
     * @param model an instance of a Data Model.
     * @param condition a strings of condition to perform update on.
     * @param params value of each condition in order.
     * @param <T> type of the data model to update.
     * @return the count of updated rows.
     * @throws IllegalAccessException When missing an attribute marked with {@link PrimaryField} annotation or that attribute's value is missing.
     * @throws IllegalArgumentException when a field marked with {@link NotNullField} is missing its value.
     * @throws SQLException when there is an error occurred during data update.
     */
    private <T> int updateSQL(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException, SQLException {
        if (sqlDBQuery == null) throw new IllegalAccessException("Calling an SQL method without an SQL scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        // Prepare primary key, value to update and the condition

        List<Object> val = new ArrayList<>();
        StringBuilder setTerm = new StringBuilder();
        Field primaryField = getPrimaryKeyField(modelClass);
        Object primaryKeyValue = getPrimaryKeyValue(model);

        Field[] fields = modelClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (fieldReflector.isPrimaryKeyField(field)) continue;

            if (field.isAnnotationPresent(MongoOnly.class) || field.isAnnotationPresent(AutomaticField.class) || Modifier.isTransient(field.getModifiers())) continue;

            if (fieldReflector.isMongoPrimaryKeyField(field) && !fieldReflector.isSQLPrimaryKeyField(field)) continue;

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
        String tableName = ((DataModel<?>) model).getTableName();
        String query;

        if (condition != null  && !condition.isBlank()) {
            query = "update " + tableName + " set " + setTerm + " where " + condition;
            val.addAll(List.of(params));
        } else {
            if (primaryField == null || primaryKeyValue == null) {
                throw new IllegalAccessException("Missing value for primary key or the key field itself!");
            }

            query = "update " + tableName + " set " + setTerm + " where " + primaryField.getName() + " = ?";

            val.add(primaryKeyValue);
        }

        return sqlDBQuery.setDataSQL(query, val.toArray());
    }

    /**
     * A method invokes {@link MongoDBQuery#update(Document, Document)}
     * to update data from an {@code Object} model base on conditions,
     * @param model an instance of a Data Model.
     * @param condition a strings of condition to perform update on.
     * @param params value of each condition in order.
     * @param <T> type of the data model to update.
     * @return the count of updated rows.
     * @throws IllegalAccessException Calling this method outside of MongoDB scope.
     * @throws IllegalArgumentException when a field marked with {@link NotNullField} is missing its value.
     */
    private <T> int updateMongo(T model, String condition, Object... params) throws IllegalAccessException, IllegalArgumentException {
        if (mongoDBQuery == null) throw new IllegalAccessException("Calling a MongoDB method without a MongoDB scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        Document filter = new Document();

        if (condition != null && !condition.isBlank()) {
            int filterArgCount = Utility.countFilterParams(condition);

            if (params.length < filterArgCount) {
                throw new IllegalArgumentException("Not enough filter parameters for declared filter argument");
            }

            filter = Document.parse(Utility.appendPlaceholderValue(condition, params, filterArgCount));
        } else {
            String primaryKeyName = getPrimaryKeyField(modelClass).getName();
            Object primaryKeyVal = getPrimaryKeyValue(model);

            if (primaryKeyVal == null) {
                throw new IllegalAccessException("Missing value for primary key field: " + primaryKeyName);
            }

            filter.append(primaryKeyName, primaryKeyVal);
        }

        Document updateFields = new Document();
        Field _idField = getPrimaryKeyField(modelClass);
        String collectionName = ((DataModel<?>) model).getCollectionName();
        Field[] fields = modelClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(AutomaticField.class) || field.isAnnotationPresent(MySQLOnly.class) || Modifier.isTransient(field.getModifiers())) continue;

            if (field.getName().equals(_idField.getName())) continue;

            if (fieldReflector.isSQLPrimaryKeyField(field) && !fieldReflector.isMongoPrimaryKeyField(field)) continue;

            Object fieldValue = getFieldValue(model, field);

            if (fieldValue != null) {
                updateFields.append(field.getName(), fieldValue);
            }
        }

        if (updateFields.isEmpty()) {
            throw new IllegalArgumentException("No target field for updating specified.");
        }

        return mongoDBQuery.setMongoData(collectionName).update(filter, new Document("$set", updateFields)).count();
    }

    private <T> Object getFieldValue(T model, Field field) throws IllegalAccessException {
        return fieldReflector.getFieldValue(model, field);
    }

    private Field getPrimaryKeyField(Class<?> modelClass) {
        return fieldReflector.getPrimaryKeyField(modelClass);
    }

    private <T> Object getPrimaryKeyValue(T model) throws IllegalAccessException {
        return fieldReflector.getPrimaryKeyValue(model);
    }
}
