package dbConnect;

import dbConnect.execution.DeleteParser;
import dbConnect.execution.InsertParser;
import dbConnect.execution.RetrieveParser;
import dbConnect.execution.UpdateParser;

import java.sql.SQLException;
import java.util.List;

/**
 * <div>
 * Database Connector class.<br>
 * </div>
 * <div>
 * This class contains:
 * <ul>
 *      <li>{@link #initialize()} initialize DBConnect using default value.</li>
 *      <li>{@link #initialize(String)} initialize DBConnect for a local database server.</li>
 *      <li>{@link #initialize(String, int, String, String)} overload local connection with custom details.</li>
 *      <li>{@link #initialize(String, String, int, String, String)} initialize DBConnect with fully customizable details.</li>
 *      <li>{@link #initCheck()} check if {@link DBConnect} is initialized yet.</li>
 *      <li>{@link #retrieve(Class, String, Object...)} get certain data from a table.</li>
 *      <li>{@link #retrieveAll(Class)} get all data from a certain table.</li>
 *      <li>{@link #insert(Object)} insert data to a certain table.</li>
 *      <li>{@link #update(Object)} update data to a certain row in a table.</li>
 *      <li>{@link #delete(Object)} delete data from a certain row in a table.</li>
 * </ul>
 * </div>
 */

public class DBConnect {
    /**
     * A static instance of {@link dbConnect.DBQuery}.
     */
    private static DBQuery dbQuery;

    /**
     * Initialization of Database connection.<br>
     * This overload fetch default value from ConnectorString.
     * <ul>
     *      <li>host: {@code localhost}</li>
     *      <li>database: {@code store_db}</li>
     *      <li>port: {@code 3306}</li>
     *      <li>user: {@code root}</li>
     *      <li>password: {@code root}</li>
     * </ul>
     * <i><strong>Initialize method should be call before all database query tasks.</strong></i>
     */
    public static void initialize() {
        ConnectorString defaultConn = ConnectorString.defaultConn();
        dbQuery = new DBQuery(defaultConn.getConnString(), defaultConn.getUser(), defaultConn.getPassword());
    }

    /**
     * Initialization of Database connection.<br>
     * This overload creates default connection to localhost.
     * <div>
     * Default values:
     * <ul>
     *      <li>host: {@code localhost}</li>
     *      <li>port: {@code 3306}</li>
     *      <li>user: {@code root}</li>
     *      <li>password: {@code root}</li>
     * </ul>
     * </div>
     * <i><strong>Initialize method should be call before all database query tasks.</strong></i>
     *
     * @param databaseName the {@code Name} of the database.
     *
     */
    public static void initialize(String databaseName) {
        ConnectorString connectorString = ConnectorString.setDefaultLocalConnection(databaseName);
        dbQuery = new DBQuery(connectorString.getConnString(), connectorString.getUser(), connectorString.getPassword());
    }

    /**
     * Initialization of Database connection. <br>
     * This overload creates a connection to localhost.
     * <div>
     * Default value:
     * <ul>
     *     <li>host: {@code localhost}</li>
     * </ul>
     * </div>
     * <i><strong>Initialize method should be called before all database query tasks.</strong></i>
     *
     * @param databaseName the {@code Name} of the database.
     * @param portNumber the {@code Port} of the database.
     * @param user the {@code Username} of database account.
     * @param password the {@code Password} of database account.
     *
     */
    public static void initialize(String databaseName, int portNumber, String user, String password) {
        ConnectorString connectorString = ConnectorString.setCustomLocalConnection(databaseName, portNumber, user, password);
        dbQuery = new DBQuery(connectorString.getConnString(), connectorString.getUser(), connectorString.getPassword());
    }

    /**
     * Initialization of Database connection. <br>
     * This overload creates a connection to a database at a certain host.<br>
     * <i><strong>Initialize method should be call before all database queries task.</strong></i>
     *
     * @param host the {@code address} of the database's host machine.
     * @param databaseName the {@code Name} of the database.
     * @param portNumber the {@code Port} of the database.
     * @param user the {@code Username} of database account.
     * @param password the {@code Password} of database account.
     *
     */
    public static void initialize(String host, String databaseName, int portNumber, String user, String password) {
        ConnectorString connectorString = ConnectorString.setConnection(host, databaseName, portNumber, user, password);
        dbQuery = new DBQuery(connectorString.getConnString(), user, password);
    }

    /**
     * Initialization check of Database connection.
     * @throws IllegalStateException when a user forgot to call initialization method.
     *
     */
    private static void initCheck() {
        if (dbQuery == null) {
            throw new IllegalStateException("DBConnect is not initialized.\nPlease initialize it first!");
        }
    }

    /**
     * A method to get data from the database. It uses the input class to determine what table to pull from.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to check if {@link #dbQuery} is null or not, then create instance of {@link dbConnect.execution.RetrieveParser}.<br>
     * - Invoke {@link dbConnect.execution.RetrieveParser#retrieve(Class, String, Object...)}
     * </div>
     *
     * @param modelClass a user desired {@code DataModel} class.<br>
     *                   Call ({@code DataModel.class}).
     * @param whereTerm conditions on how to search, using {@code ?} as placeholders.
     * @param params value of mentioned conditions in order.
     *
     * @return List of the desired object. If no data is found, an empty list is returned.
     * @throws Exception When there is error during retrieval of data.
     */
    public static <T> List<T> retrieve(Class<T> modelClass, String whereTerm, Object... params) {
        initCheck();
        RetrieveParser retrieveParser = new RetrieveParser(dbQuery);

        try {
            return retrieveParser.retrieve(modelClass, whereTerm, params);
        } catch (Exception e) {
            System.out.println("Failure during data selection: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * A method to get data from the database. It uses the input class to determine what table to pull from.
     * This method allows pulling all data of a model.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to check if {@link #dbQuery} is null or not, then create instance of {@link dbConnect.execution.RetrieveParser}.<br>
     * - Invoke {@link dbConnect.execution.RetrieveParser#retrieveAll(Class)}
     * </div>
     *
     * @param modelClass a user desired {@code DataModel} class.<br>
     *                   Call ({@code DataModel.class}).
     *
     * @return List of the desired object. If no data is found, an empty list is returned.
     * @throws Exception When there is error during retrieval of data.
     */
    public static <T> List<T> retrieveAll(Class<T> modelClass) {
        return retrieve(modelClass, null);
    }

    /**
     * A method to insert data to the database. It uses the input class to determine what table to push to.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to check if {@link #dbQuery} is null or not, then create instance of {@link dbConnect.execution.InsertParser}.<br>
     * - Invoke {@link dbConnect.execution.InsertParser#insert(Object)}
     * </div>
     *
     * @param model a user desired {@code dataModel} object, carrying data that need to be inserted.
     *
     * @return {@code true} if insert successfully.<br>
     *          {@code false} if insert failed. <br>
     *          Insert successful state is determined by the inserted row count.
     * @throws Exception When there is error during insert of data.
     */
    public static <T> boolean insert(T model) {
        initCheck();
        InsertParser insertParser = new InsertParser(dbQuery);

        try {
            int successRow = insertParser.insert(model);
            return successRow > 0;
        } catch (SQLException | IllegalAccessException e) {
            System.out.println("Failure during insertion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method to update data to the database. It uses the input class to determine what row in a table to update.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to check if {@link #dbQuery} is null or not, then create instance of {@link dbConnect.execution.UpdateParser}.<br>
     * - Invoke {@link dbConnect.execution.UpdateParser#update(Object)}
     * </div>
     *
     * @param model a user desired {@code dataModel} object, carrying data that need to be updated.
     *
     * @return {@code true} if update successfully.<br>
     *          {@code false} if update failed. <br>
     *          Update successful state is determined by the updated row count.
     * @throws Exception When there is error during update of data.
     */
    public static <T> boolean update(T model) {
        initCheck();

        UpdateParser updateParser = new UpdateParser(dbQuery);

        try {
            int successUpdate = updateParser.update(model);
            return  successUpdate > 0;
        } catch (SQLException | IllegalAccessException e) {
            System.out.println("Failure during update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method to delete data to the database. It uses the input class to determine what row in a table to delete.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to check if {@link #dbQuery} is null or not, then create instance of {@link dbConnect.execution.DeleteParser}.<br>
     * - Invoke {@link dbConnect.execution.DeleteParser#delete(Object)}
     * </div>
     *
     * @param model a user desired {@code dataModel} object, must contain at least the primary key field initiated.
     *
     * @return {@code true} if delete successfully.<br>
     *          {@code false} if delete failed. <br>
     *          delete successful state is determined by the deleted row count.
     * @throws Exception When there is error during update of data.
     */
    public static <T> boolean delete(T model) {
        initCheck();
        DeleteParser deleteParser = new DeleteParser(dbQuery);

        try {
            int successRow = deleteParser.delete(model);
            return successRow > 0;
        } catch (SQLException | IllegalAccessException e) {
            System.out.println("Failure during deletion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
