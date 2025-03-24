package dbConnect.execution;

import dbConnect.mapper.DocumentInterface;
import dbConnect.mapper.MongoMapper;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.mapper.SQLMapper;
import dbConnect.models.enums.Collection;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;
import dbConnect.models.enums.Table;
import org.bson.Document;

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

    public <T> List<T> retrieve(Class<T> modelClass, String whereTerm, Object... params) throws IllegalAccessException, SQLException {
        if (mongoDBQuery == null) {
            return retrieveSQL(modelClass, whereTerm, params);
        } else if (sqlDBQuery == null) {
            return retrieveMongo(modelClass, whereTerm, params);
        } else {
            return null;
        }
    }

    /**
     * A method invokes {@link SqlDBQuery#loadSQLData(String, SQLMapper, Object...)}
     * to fetch data from a {@code Class} model .
     * @param modelClass a Data Model Class.
     *                   It must contain a method call {@code getTable()}.
     *                   It must contain a method call {@code getMap()}.
     * @param whereTerm conditions used for the search.
     *                  Values are stored in {@code params} and is parsed where placeholder marking {@code ?} is placed.
     * @param params values of {@code whereTerm} store in corresponding order.
     * @return a List of instances specified by the data model class that met the {@code whereTerm} conditions.
     * @param <T> Class
     * @throws IllegalAccessException when missing {@code getTable()} or {@code getMap()} method from the data model.
     * @throws SQLException when there is an error occurred during data selection.
     */
    private <T> List<T> retrieveSQL(Class<T> modelClass, String whereTerm, Object... params) throws IllegalAccessException, SQLException {
        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTable() method that return a Table enum.");
        }

        ResultSetInterface<T> mapper;

        try {
            mapper = (ResultSetInterface<T>) modelClass.getMethod("getTableMap").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTableMap() method that return a new instant of mapping method.");
        }

        String query = "select * from " + table.getName();
        if (whereTerm != null && !whereTerm.trim().isEmpty()) {
            query += " where " + whereTerm;
        }

        SQLMapper<T> sqlMapper = new SQLMapper<>(mapper);

        return sqlDBQuery.loadSQLData(query, sqlMapper, params);
    }

    private <T> List<T> retrieveMongo(Class<T> modelClass, String jsonFilter, Object... params) throws IllegalAccessException {
        Collection collection;

        try {
            collection = (Collection) modelClass.getMethod("getCollection").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getCollection() method that return a Table enum.");
        }

        DocumentInterface<T> mapper;

        try {
            mapper = (DocumentInterface<T>) modelClass.getMethod("getCollectionMap").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getCollectionMap() method that return a new instant of mapping method.");
        }

        Document filter = Document.parse(jsonFilter);

        Document projection = new Document();

        MongoMapper<T> mongoMapper = new MongoMapper<>(mapper);

        return mongoDBQuery.loadMongoData(collection.getName(), filter, projection, mongoMapper);
    }

    /**
     * A method invokes {@link SqlDBQuery#loadSQLData(String, SQLMapper, Object...)}
     * to fetch all data from a {@code Class} model.
     * This simply invoke {@link #retrieve(Class, String, Object...)} with no {@code whereTerm} condition.
     * @param modelClass a Data Model Class.
     *                   It must contain a method call {@code getTable()}.
     *                   It must contain a method call {@code getMap()}.
     * @return a List of all instances specified by the data model class.
     * @param <T> Class
     * @throws IllegalAccessException when missing {@code getTable()} or {@code getMap()} method from the data model.
     * @throws SQLException when there is an error occurred during data selection.
     */
    public <T> List<T> retrieveAll(Class<T> modelClass) throws IllegalAccessException, SQLException {
        return retrieve(modelClass, null);

    }
}
