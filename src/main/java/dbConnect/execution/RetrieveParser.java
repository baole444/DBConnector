package dbConnect.execution;

import dbConnect.DataModel;
import dbConnect.Utility;
import dbConnect.mapper.DocumentInterface;
import dbConnect.map.MongoMap;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.map.SQLMap;
import dbConnect.models.enums.Collection;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.enums.Table;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * Handle retrieval query (select query) parsing using reflection.
 * This class also contains overload for getting all data.
 */
public class RetrieveParser {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;


    /**
     * Constructor of {@link RetrieveParser}.
     * For noSQL query, see {@link #RetrieveParser(MongoDBQuery)}
     * @param sqlDBQuery an instance of {@link SqlDBQuery#SqlDBQuery(String, String, String)}
     */
    public RetrieveParser(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
    }

    /**
     * Constructor of {@link RetrieveParser}.
     * For SQL query, see {@link #RetrieveParser(SqlDBQuery)}
     * @param mongoDBQuery an instance of {@link MongoDBQuery#MongoDBQuery(String, String)}
     */
    public RetrieveParser(MongoDBQuery mongoDBQuery) {
        this.mongoDBQuery = mongoDBQuery;
        this.sqlDBQuery = null;
    }

    /**
     * A method to determine the correct inner retrieve method.
     * @param modelClass a data model class extending {@link DataModel}.
     * @param condition a set of conditions used for the query.
     * @param params parameters of the conditions in order and optional projection field (NoSQL only).
     * @return a List of instances specified by the data model class that met the {@code condition} conditions.
     * @param <T> a data model class extending {@link dbConnect.DataModel}
     */
    public <T> List<T> retrieve(Class<T> modelClass, String condition, Object... params) throws IllegalAccessException, SQLException {
        if (mongoDBQuery == null) {
            return retrieveSQL(modelClass, condition, params);
        } else if (sqlDBQuery == null) {
            return retrieveMongo(modelClass, condition, params);
        } else {
            return null;
        }
    }

    /**
     * A method invokes {@link SqlDBQuery#loadSQLData(String, SQLMap, Object...)}
     * to fetch data for a {@link dbConnect.DataModel} model.
     * <p>
     * @param modelClass a Data Model Class.
     *                   It must contain a method call {@link DataModel#getTable()}.
     *                   It must contain a method call {@link DataModel#getTableMap()}.
     * </p>
     * <p>
     * @param whereTerm conditions used for the search.
     *                  Values are stored in {@code params} and is parsed where placeholder marking {@code ?} is placed.
     * </p>
     * @param params values of {@code whereTerm} store in corresponding order.
     * @return a List of instances specified by the data model class that met the {@code whereTerm} conditions.
     * @param <T> a data model class extending {@link dbConnect.DataModel}
     * @throws IllegalAccessException when missing {@link DataModel#getTable()} or {@link DataModel#getTableMap()} method from the data model
     * or accessing the method outside SQL scope.
     * @throws SQLException when there is an error occurred during data selection.
     */
    private <T> List<T> retrieveSQL(Class<T> modelClass, String whereTerm, Object... params) throws IllegalAccessException, SQLException {
        if (sqlDBQuery == null) throw new IllegalAccessException("Calling an SQL method without an SQL scope!");

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        Table table;

        T instance;

        try {
            instance = modelClass.getDeclaredConstructor().newInstance();
        }  catch (InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException("Model '" + modelClass.getName() + "' is missing an empty Constructor.");
        }

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(instance);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getTable() method that return a Table enum.");
        }

        ResultSetInterface<T> mapper;

        try {
            mapper = (ResultSetInterface<T>) modelClass.getMethod("getTableMap").invoke(instance);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getTableMap() method that return a new instant of mapping method.");
        }

        String query = "select * from " + table.getName();
        if (whereTerm != null && !whereTerm.trim().isEmpty()) {
            query += " where " + whereTerm;
        }

        SQLMap<T> sqlMapper = new SQLMap<>(mapper);

        return sqlDBQuery.loadSQLData(query, sqlMapper, params);
    }

    /**
     * A method invoke {@link MongoDBQuery#loadMongoData(String, Document, Document, MongoMap)}
     * to fetch data for a {@link dbConnect.DataModel} model.
     * @param modelClass a Data Model Class.
     *                   It must contain a method call {@link DataModel#getCollection()}.
     *                   It must contain a method call {@link DataModel#getCollectionMap()}.
     * </p>
     * @param condition conditions used for the search.
     *                   Values are stored in {@code params} and is parsed where placeholder marking {@code ?} is placed.
     * @param params values of {@code jsonFilter} store in corresponding order, the last params can be used for projection.
     * @return a List of instances specified by the data model class that met the {@code jsonFilter} conditions.
     * @param <T> a data model class extending {@link dbConnect.DataModel}
     * @throws IllegalAccessException when missing {@link DataModel#getCollection()} or {@link DataModel#getCollectionMap()} method from the data model
     * or accessing the method outside NoSQL scope.
     */
    private <T> List<T> retrieveMongo(Class<T> modelClass, String condition, Object... params) throws IllegalAccessException {
        if (mongoDBQuery == null) throw new IllegalAccessException("Calling a MongoDB method without a MongoDB scope!");

        if (!DataModel.class.isAssignableFrom(modelClass)) {
            System.out.println("Warning: '" + modelClass.getName() + " does not extend DataModel, which could lead to missing essential methods.");
        }

        Collection collection;

        T instance;

        try {
            instance = modelClass.getDeclaredConstructor().newInstance();
        }  catch (InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException("Model '" + modelClass.getName() + "' is missing an empty Constructor.");
        }

        try {
            collection = (Collection) modelClass.getMethod("getCollection").invoke(instance);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getCollection() method that return a Collection enum.");
        }

        DocumentInterface<T> mapper;

        try {
            mapper = (DocumentInterface<T>) modelClass.getMethod("getCollectionMap").invoke(instance);
        } catch (Exception e) {
            throw new IllegalAccessException("Model '" + modelClass.getName() + "' is missing a valid getCollectionMap() method that return a new instant of mapping method.");
        }

        Document projection = new Document();

        Document filter = new Document();

        if (condition != null && !condition.isBlank()) {
            int filterArgCount = Utility.countFilterParams(condition);

            if (params.length < filterArgCount) {
                throw new IllegalArgumentException("Not enough filter parameters for declared filter argument");
            }

            if (params.length > filterArgCount && params[params.length -1] instanceof String) {
                projection = Document.parse((String) params[params.length - 1]);

                Object[] filterParams = new Object[filterArgCount];
                System.arraycopy(params, 0, filterParams, 0, filterArgCount);

                params = filterParams;
            }

            filter = Document.parse(Utility.appendPlaceholderValue(condition, params, filterArgCount));
        }

        MongoMap<T> mongoMapper = new MongoMap<>(mapper);

        return mongoDBQuery.loadMongoData(collection.getName(), filter, projection, mongoMapper);
    }

    /**
     * A method to retrieve all entries of a Data model
     * This simply invoke {@link #retrieve(Class, String, Object...)} with no {@code condition}.
     * @param modelClass a data model class extending {@link DataModel}.
     * @return a List of all instances specified by the data model class.
     * @param <T> a data model class extending {@link dbConnect.DataModel}
     */
    public <T> List<T> retrieveAll(Class<T> modelClass) throws IllegalAccessException, SQLException {
        return retrieve(modelClass, null);
    }
}
