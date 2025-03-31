package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.constrain.MySQLOnly;
import dbConnect.models.enums.Collection;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;
import org.bson.Document;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle insert query parsing using reflection.
 */
public class InsertParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;

    /**
     * Constructor of {@link InsertParser}.
     * For noSQL query, see {@link #InsertParser(MongoDBQuery)}
     * @param sqlDBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public InsertParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
    }

    /**
     * Constructor of {@link InsertParser}.
     * For SQL query, see {@link #InsertParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public InsertParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
    }

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
     * @param model an instance of a Data Model. It must contain a method call {@code getTable()}.
     * @return the count of inserted rows.
     * @param <T> Object
     * @throws IllegalAccessException when missing {@code getTable()} method from the data model.
     * @throws SQLException when there is an error occurred during data insertion.
     */
    public <T> int insertSQL(T model) throws IllegalAccessException, SQLException {
        if (sqlDBQuery == null) throw new IllegalAccessException("Calling an SQL method without an SQL scope!");

        Class<?> modelClass = model.getClass();

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        // Get table name using reflection
        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(model);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getTable() method that return a Table enum.");
        }

        // Building SQL command
        Field[] fields = modelClass.getDeclaredFields();
        List<Object> val = new ArrayList<>();
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);

            // Ignore auto generated and mongoDB only fields
            if (field.isAnnotationPresent(AutomaticField.class) || field.isAnnotationPresent(MongoOnly.class)) { continue; }

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

        String query = "insert into " + table.getName() + " (" + columns + ") values (" + placeholders + ")";

        return sqlDBQuery.setDataSQL(query, val.toArray());
    }

    public <T> int insertMongo(T model) throws IllegalAccessException {
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

        Field[] fields = modelClass.getDeclaredFields();
        Document document = new Document();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(AutomaticField.class) || field.isAnnotationPresent(MySQLOnly.class)) { continue; }

            Object fieldValue = getFieldValue(model, field);
            document.append(field.getName(),fieldValue);
        }

        return mongoDBQuery.setMongoData(collection.getName()).insert(document).count();
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
        // Verify value for not null field
        if (field.isAnnotationPresent(NotNullField.class)) {
            if (fieldValue == null) {
                throw new IllegalArgumentException("Missing value for field: " + field.getName() + " with not null annotation");
            }
        }

        // Trim string that passes annotation's limit
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

}
