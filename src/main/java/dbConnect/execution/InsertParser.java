package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.constrain.MySQLOnly;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.AutomaticField;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle insert query parsing using reflection.
 */
public class InsertParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;
    private final FieldReflector fieldReflector;

    /**
     * Constructor of {@link InsertParser}.
     * For noSQL query, see {@link #InsertParser(MongoDBQuery)}
     * @param sqlDBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public InsertParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
        this.fieldReflector = new FieldReflector(sqlDBQuery);
    }

    /**
     * Constructor of {@link InsertParser}.
     * For SQL query, see {@link #InsertParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public InsertParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
        this.fieldReflector = new FieldReflector(mongoDBQuery);
    }

    /**
     * A method to determine the inner insert method.
     * @param model an instance of a Data Model
     * @return the count of inserted rows.
     * @param <T> the data model to perform insert to.
     * @throws IllegalAccessException data model class is missing required method.
     * @throws SQLException error while performing MySQL query.
     */
    public <T> int insert(T model) throws IllegalAccessException, SQLException {
        if (mongoDBQuery == null) {
            return insertSQL(model);
        } else if (sqlDBQuery == null) {
            return insertMongo(model);
        } else {
            return -1;
        }
    }

    /**
     * A method invokes {@link SqlDBQuery#setDataSQL(String, Object...)}
     * to insert data from an {@code Object} model.
     * @param model an instance of a Data Model. It must have at least {@link dbConnect.models.meta.TableName} annotation.
     * @return the count of inserted rows.
     * @param <T> the data model to perform insert to.
     * @throws IllegalAccessException when calling this method outside of SQL scope.
     * @throws SQLException when there is an error occurred during data insertion.
     */
    private <T> int insertSQL(T model) throws IllegalAccessException, SQLException {
        if (sqlDBQuery == null) throw new IllegalAccessException("Calling an SQL method without an SQL scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        // Building SQL command
        String tableName = ((DataModel<?>) model).getTableName();
        Field[] fields = modelClass.getDeclaredFields();
        List<Object> val = new ArrayList<>();
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);

            // Ignore auto generated and mongoDB only fields
            if (field.isAnnotationPresent(AutomaticField.class) || field.isAnnotationPresent(MongoOnly.class) || Modifier.isTransient(field.getModifiers())) continue;

            if (fieldReflector.isMongoPrimaryKeyField(field) && !fieldReflector.isSQLPrimaryKeyField(field)) continue;

            Object fieldValue = getFieldValue(model, field);

            columns.append(field.getName()).append(", ");
            placeholders.append("?, ");
            val.add(fieldValue);
        }

        // Trim command and space at the end
        if (!columns.isEmpty()) {
            columns.setLength(columns.length() - 2);
            placeholders.setLength(placeholders.length() -2);
        }

        String query = "insert into " + tableName + " (" + columns + ") values (" + placeholders + ")";

        return sqlDBQuery.setDataSQL(query, val.toArray());
    }

    private <T> int insertMongo(T model) throws IllegalAccessException {
        if (mongoDBQuery == null) throw new IllegalAccessException("Calling a MongoDB method without a MongoDB scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        String collectionName = ((DataModel<?>) model).getCollectionName();
        Field[] fields = modelClass.getDeclaredFields();
        Document document = new Document();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(AutomaticField.class) || field.isAnnotationPresent(MySQLOnly.class) || Modifier.isTransient(field.getModifiers())) continue;

            if (fieldReflector.isSQLPrimaryKeyField(field) && !fieldReflector.isMongoPrimaryKeyField(field)) continue;

            Object fieldValue = getFieldValue(model, field);

            if (fieldValue != null) document.append(field.getName(),fieldValue);
        }

        return mongoDBQuery.setMongoData(collectionName).insert(document).count();
    }

    private <T> Object getFieldValue(T model, Field field) throws IllegalAccessException {
        return fieldReflector.getFieldValue(model, field);
    }
}
