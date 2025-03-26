package dbConnect.query;

import dbConnect.mapper.MongoMapper;
import dbConnect.mapper.SQLMapper;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <div>
 * Database query class.<br>
 * Constructor:
 * <ul>
 *      <li>{@link #SqlDBQuery(String, String, String)} initialize DBQuery</li>
 * </ul>
 * </div>
 * <div>
 * This class contains:
 * <ul>
 *      <li>{@link #loadSQLData(String, SQLMapper, Object...)} fetching data from database server.</li>
 *      <li>{@link #setDataSQL(String, Object...)} insert or modify data from database server.</li>
 * </ul>
 * </div>
 */

public class SqlDBQuery implements DBInterface {
    private final String dbUrl;
    private final String user;
    private final String password;

    /**
     * Constructor of {@link SqlDBQuery}.
     * @param dbUrl link to the database server.
     * @param user username of the account.
     * @param password account's password.
     */
    public SqlDBQuery(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }

    /**
     * A low level method to fetch data from a database server.
     * @param query SQL script, often with placeholders.
     * @param model A Mapping method of a Data Model Class. It can be obtained via {@code getMap()}.
     * @param params Values for placeholders in corresponding order.
     * @return {@link List} of instances of a specified data model.
     * @param <T> Object
     * @throws SQLException when there is an error occurred during execution.
     */
    @Override
    public <T> List<T> loadSQLData(String query, SQLMapper<T> model, Object... params) throws SQLException {
        List<T> rows = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             PreparedStatement statement = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    rows.add(model.map(resultSet));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return rows;
    }

    /**
     * A low level method to update data with a database server.
     * @param query SQL script, often with placeholders.
     * @param params Values for placeholders in corresponding order.
     * @return count of successful execution.
     * @throws SQLException when there is an error occurred during execution.
     */
    @Override
    public int setDataSQL(String query, Object... params) throws SQLException{
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             PreparedStatement preparedStatement = conn.prepareStatement(query);) {

            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }

            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }

    @Override
    public MongoDBQuery setMongoData(String collectionName) {
        throw new UnsupportedOperationException("MongoDB operation not allowed in SQL queries.");
    }

    @Override
    public <T> List<T> loadMongoData(String collectionName, Document filter, Document projection, MongoMapper<T> model) {
        throw new UnsupportedOperationException("MongoDB operation not allowed in SQL queries.");
    }

}
