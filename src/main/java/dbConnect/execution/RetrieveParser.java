package dbConnect.execution;

import dbConnect.DBMapper;
import dbConnect.DBQuery;
import dbConnect.models.enums.Table;

import java.sql.SQLException;
import java.util.List;

/**
 * Handle retrieval query (select query) parsing using reflection.
 * This class also contains overload for getting all data.
 */
public class RetrieveParser {
    private final DBQuery dbQuery;

    /**
     * Constructor of {@link RetrieveParser}.
     * @param dbQuery an instance of {@link DBQuery#DBQuery(String, String, String)}
     */
    public RetrieveParser(DBQuery dbQuery) {
        this.dbQuery = dbQuery;
    }

    /**
     * A method invokes {@link DBQuery#loadData(String, DBMapper, Object...)}
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
    public <T> List<T> retrieve(Class<T> modelClass, String whereTerm, Object... params) throws IllegalAccessException, SQLException {
        Table table;

        try {
            table = (Table) modelClass.getMethod("getTable").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getTable() method that return a Table enum.");
        }

        DBMapper<T> mapper;

        try {
            mapper = (DBMapper<T>) modelClass.getMethod("getMap").invoke(null);
        } catch (Exception e) {
            throw new IllegalAccessException("Model is missing a valid getMap() method that return a new instant of mapping method.");
        }

        String query = "select * from " + table.getName();
        if (whereTerm != null && !whereTerm.trim().isEmpty()) {
            query += " where " + whereTerm;
        }

        return dbQuery.loadData(query, mapper, params);
    }

    /**
     * A method invokes {@link DBQuery#loadData(String, DBMapper, Object...)}
     * to fetch all data from a {@code Class} model.
     * This simply involk {@link #retrieve(Class, String, Object...)} with no {@code whereTerm} condition.
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
