package dbConnect;

import dbConnect.execution.DeleteParser;
import dbConnect.execution.InsertParser;
import dbConnect.execution.RetrieveParser;
import dbConnect.execution.UpdateParser;
import dbConnect.query.ConnectorString;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * <div>
 * Database Connector class.<br>
 * </div>
 * <div>
 * This class contains:
 * <ul>
 *      <li>{@link #initializeSQL()} initialize DBConnect using default value.</li>
 *      <li>{@link #initializeSQL(String)} initialize DBConnect for a local database server.</li>
 *      <li>{@link #initializeSQL(String, int, String, String)} overload local connection with custom details.</li>
 *      <li>{@link #initializeSQL(String, String, int, String, String)} initialize DBConnect with fully customizable details.</li>
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
     * A static instance of {@link SqlDBQuery}.
     */
    private static SqlDBQuery SQLdBQuery;

    /**
     * A static instance of {@link MongoDBQuery}.
     */
    private static MongoDBQuery MongoDBQuery;

    /**
     * Initialization of MySQL Database connection.<br>
     * This overload fetches the default value from ConnectorString.
     * <ul>
     *      <li>host: {@code localhost}</li>
     *      <li>database: {@code store_db}</li>
     *      <li>port: {@code 3306}</li>
     *      <li>user: {@code root}</li>
     *      <li>password: {@code root}</li>
     * </ul>
     * <i><strong>Initialize method should be call before all database query tasks.</strong></i>
     */
    public static void initializeSQL() {
        ConnectorString defaultConn = ConnectorString.loadDefaultSQLConnection();
        SQLdBQuery = new SqlDBQuery(defaultConn.getSQLConnectionString(), defaultConn.getUser(), defaultConn.getPassword());
        MongoDBQuery = null;
    }

    /**
     * Initialization of Mongo Database connection.<br>
     * This overload fetches the default value from ConnectorString.
     * <ul>
     *      <li>host: {@code localhost}</li>
     *      <li>database: {@code store_db}</li>
     *      <li>port: {@code 27017}</li>
     * </ul>
     * <i><strong>Initialize method should be call before all database query tasks.</strong></i>
     */
    public static void initializeMongo() {
        ConnectorString defaultConn = ConnectorString.loadDefaultMongoConnection();
        MongoDBQuery = new MongoDBQuery(defaultConn.getMongoConnectionString(), defaultConn.getDatabaseName());
        SQLdBQuery = null;
    }

    /**
     * Initialization of MySQL Database connection.<br>
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
    public static void initializeSQL(String databaseName) {
        ConnectorString connectorString = ConnectorString.setDefaultLocalSQLConnection(databaseName);
        SQLdBQuery = new SqlDBQuery(connectorString.getSQLConnectionString(), connectorString.getUser(), connectorString.getPassword());
        MongoDBQuery = null;
    }

    /**
     * Initialization of Mongo Database connection.<br>
     * This overload creates default connection to localhost.
     * <div>
     * <ul>
     *      <li>host: {@code localhost}</li>
     *      <li>database: {@code store_db}</li>
     *      <li>port: {@code 27017}</li>
     * </ul>
     * </div>
     * <i><strong>Initialize method should be call before all database query tasks.</strong></i>
     *
     * @param databaseName the {@code Name} of the database.
     *
     */
    public static void initializeMongo(String databaseName) {
        ConnectorString connectorString = ConnectorString.setDefaultLocalMongoConnection(databaseName);
        MongoDBQuery = new MongoDBQuery(connectorString.getMongoConnectionString(), connectorString.getDatabaseName());
        SQLdBQuery = null;
    }

    /**
     * Initialization of MySQL Database connection. <br>
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
    public static void initializeSQL(String databaseName, int portNumber, String user, String password) {
        ConnectorString connectorString = ConnectorString.setCustomLocalSQLConnection(databaseName, portNumber, user, password);
        SQLdBQuery = new SqlDBQuery(connectorString.getSQLConnectionString(), connectorString.getUser(), connectorString.getPassword());
        MongoDBQuery = null;
    }

    /**
     * Initialization of MySQL Database connection. <br>
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
    public static void initializeSQL(String host, String databaseName, int portNumber, String user, String password) {
        ConnectorString connectorString = ConnectorString.setSQLConnection(host, databaseName, portNumber, user, password);
        SQLdBQuery = new SqlDBQuery(connectorString.getSQLConnectionString(), user, password);
        MongoDBQuery = null;
    }

    /**
     * Initialization check of Database connection.
     * @throws IllegalStateException when a user forgot to call initialization method.
     */
    private static void initCheck() {
        if (SQLdBQuery == null && MongoDBQuery == null) {
            throw new IllegalStateException("DBConnect is not initialized.\nPlease call initializeSQL first!");
        }
    }

    /**
     * A method to get data from the database.
     * It uses the input class to determine where to pull data from.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to validate scope declaration,
     * then create instance of {@link RetrieveParser}.<br>
     * - Invoke {@link RetrieveParser#retrieve(Class, String, Object...)}
     * </div>
     *
     * @param modelClass a user desired data model class extending {@link DataModel}.<br>
     *                   Call ({@code DataModel.class}).
     * @param conditions conditions on how to search, using {@code ?} as placeholders.
     * @param params value of mentioned conditions in order.
     *
     * @return List of the desired object. If no data is found, an empty list is returned.
     */
    public static <T> List<T> retrieve(Class<T> modelClass, String conditions, Object... params) {
        initCheck();
        RetrieveParser retrieveParser = null;

        if (MongoDBQuery == null && SQLdBQuery != null) {
            retrieveParser = new RetrieveParser(SQLdBQuery);
        }
        else if (MongoDBQuery != null && SQLdBQuery == null) {
            retrieveParser = new RetrieveParser(MongoDBQuery);
        }

        try {
            assert retrieveParser != null;
            return retrieveParser.retrieve(modelClass, conditions, params);
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
     * - Invoke {@link #initCheck()} to validate scope declaration, then create instance of {@link RetrieveParser}.<br>
     * - Invoke {@link RetrieveParser#retrieveAll(Class)}
     * </div>
     *
     * @param modelClass a user desired data model class extending {@link DataModel}.<br>
     *                   Call ({@code DataModel.class}).
     *
     * @return List of the desired object. If no data is found, an empty list is returned.
     */
    public static <T> List<T> retrieveAll(Class<T> modelClass) {
        return retrieve(modelClass, null);
    }

    /**
     * A method to insert data to the database. It uses the input class to determine what table to push to.
     * <div>
     * Function:<br>
     * - Invoke {@link #initCheck()} to validate scope declaration, then create instance of {@link InsertParser}.<br>
     * - Invoke {@link InsertParser#insert(Object)}
     * </div>
     *
     * @param dataModelObject a user desired a data model object extending {@link DataModel}, carrying data that need to be inserted.
     *
     * @return {@code true} if insert successfully.<br>
     *          {@code false} if insert failed. <br>
     *          Insert successful state is determined by the inserted row count.
     */
    public static <T> boolean insert(T dataModelObject) {
        initCheck();
        InsertParser insertParser = null;

        if (MongoDBQuery == null && SQLdBQuery != null) {
            insertParser = new InsertParser(SQLdBQuery);
        }
        else if (MongoDBQuery != null && SQLdBQuery == null) {
            insertParser = new InsertParser(MongoDBQuery);
        }

        try {
            assert insertParser != null;
            int successRow = insertParser.insert(dataModelObject);
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
     * - Invoke {@link #initCheck()} to check if {@link #SQLdBQuery} is null or not, then create instance of {@link UpdateParser}.<br>
     * - Invoke {@link UpdateParser#update(Object, String, Object...)}
     * </div>
     *
     * @param model a user desired {@code dataModel} object, carrying data that need to be updated.
     *
     * @return {@code true} if update successfully.<br>
     *          {@code false} if update failed. <br>
     *          Update successful state is determined by the updated row count.
     */
    public static <T> boolean update(T model) {
        initCheck();
        UpdateParser updateParser = null;

        if (MongoDBQuery == null && SQLdBQuery != null) {
            updateParser = new UpdateParser(SQLdBQuery);
        }
        else if (MongoDBQuery != null && SQLdBQuery == null) {
            updateParser = new UpdateParser(MongoDBQuery);
        }

        try {
            assert updateParser != null;
            int successUpdate = updateParser.update(model, null);
            return  successUpdate > 0;
        } catch (SQLException | IllegalAccessException e) {
            System.out.println("Failure during update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static <T> boolean update(T model, String conditions, Object... params) {
        initCheck();
        UpdateParser updateParser = null;

        if (MongoDBQuery == null && SQLdBQuery != null) {
            updateParser = new UpdateParser(SQLdBQuery);
        }
        else if (MongoDBQuery != null && SQLdBQuery == null) {
            updateParser = new UpdateParser(MongoDBQuery);
        }

        try {
            assert updateParser != null;
            int successUpdate = updateParser.update(model, conditions, params);
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
     * - Invoke {@link #initCheck()} to check if {@link #SQLdBQuery} is null or not, then create instance of {@link DeleteParser}.<br>
     * - Invoke {@link DeleteParser#delete(Object, String, Object...)}
     * </div>
     *
     * @param model a user desired {@code dataModel} object, must contain at least the primary key field initiated.
     *
     * @return {@code true} if delete successfully.<br>
     *          {@code false} if delete failed. <br>
     *          delete successful state is determined by the deleted row count.
     */
    public static <T> boolean delete(T model) {
        initCheck();
        DeleteParser deleteParser = null;

        if (MongoDBQuery == null && SQLdBQuery != null) {
            deleteParser = new DeleteParser(SQLdBQuery);
        }
        else if (MongoDBQuery != null && SQLdBQuery == null) {
            deleteParser = new DeleteParser(MongoDBQuery);
        }

        try {
            assert deleteParser != null;
            int successRow = deleteParser.delete(model, null);
            return successRow > 0;
        } catch (SQLException | IllegalAccessException e) {
            System.out.println("Failure during deletion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static <T> boolean delete(T model, String conditions, Object... params) {
        initCheck();
        DeleteParser deleteParser = null;

        if (MongoDBQuery == null && SQLdBQuery != null) {
            deleteParser = new DeleteParser(SQLdBQuery);
        }
        else if (MongoDBQuery != null && SQLdBQuery == null) {
            deleteParser = new DeleteParser(MongoDBQuery);
        }
        try {
            assert deleteParser != null;
            int successRow = deleteParser.delete(model, conditions, params);
            return successRow > 0;
        } catch (SQLException | IllegalAccessException e) {
            System.out.println("Failure during deletion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

