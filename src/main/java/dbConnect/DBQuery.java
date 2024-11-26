package dbConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <div>
 * Database query class.<br>
 * Constructor:
 * <ul>
 *      <li>{@link #DBQuery(String, String, String)} initialize DBQuery</li>
 * </ul>
 * </div>
 * <div>
 * This class contains:
 * <ul>
 *      <li>{@link #loadData(String, DBMapper, Object...)} fetching data from database server.</li>
 *      <li>{@link #setData(String, Object...)} insert or modify data from database server.</li>
 * </ul>
 * </div>
 */

public class DBQuery {
    private String dbUrl;
    private String user;
    private String password;

    public DBQuery(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }

    public <T> List<T> loadData(String query, DBMapper<T> model, Object... params) throws SQLException {
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

    public int setData(String query, Object... params) throws SQLException{
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
}
