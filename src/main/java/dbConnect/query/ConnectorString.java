package dbConnect.query;

/**
 * Use to construct a connector string for MySQL or MongoDB.
 */
public class ConnectorString {
    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;
    private final String replicaSet;

    /**
     * Constructor for MySQL database connection.
     * @param host address of the host.
     * @param port port number of the database.
     * @param databaseName name of the database.
     * @param user the account that will be connected to the database.
     * @param password password of the account.
     */
    public ConnectorString(String host, int port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.replicaSet = null;
    }

    /**
     * Constructor for MongoDB database connection.
     * @param host address of the host.
     * @param port port number of the database.
     * @param databaseName name of the database.
     * @param user the account that will be connected to the database.
     * @param password password of the account.
     * @param replicaSet Mongo db replica set.
     */
    public ConnectorString(String host, int port, String databaseName, String user, String password, String replicaSet) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.replicaSet = replicaSet;
    }

    /**
     * Construct a MySQL connection string.
     * @return jdbc connection string.
     */
    public String getSQLConnectionString() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
    }

    /**
     * Construct a MongoDB connection string.
     * @return mongodb connection string.
     */
    public String getMongoConnectionString() {
        String authArg = "";

        if (user != null) {
            if (!user.isBlank()) authArg = user + ":" + password + "@";
        }

        String replicaArg = "";

        if (replicaSet != null) {
            replicaArg = "?replicaSet=" + replicaSet;
        }

        return "mongodb://" + authArg
                + host + ":" + port + "/"
                + databaseName + replicaArg;
    }

    /**
     * Get the current username of the connector string.
     * @return username of connecting account
     */
    public String getUser() {
        return user;
    }

    /**
     * Get the current password stored in connector string.
     * @return current user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get currently stored database's name.
     * @return name of the database
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Connect to local MySQL server with given database's name.
     * This method uses default values of MySQL connection string.
     * @param databaseName name of the database to connect to.
     * @return a ConnectorString with default values for MySQL.
     */
    public static ConnectorString setDefaultLocalSQLConnection(String databaseName) {
        return new ConnectorString("localhost", 3306, databaseName, "root", "root");
    }

    /**
     * Connect to local MongoDB server with given database's name.
     * This method uses default values of MongoDB connection string.
     * @param databaseName name of the database to connect to.
     * @return a ConnectorString with default values for MongoDB.
     */
    public static ConnectorString setDefaultLocalMongoConnection(String databaseName) {
        return new ConnectorString("localhost", 27017, databaseName, null, null, null);
    }

    /**
     * Connect to a local MySQL server.
     * @param databaseName name of the database to connect to.
     * @param port the port where this connection is located.
     * @param user the username of the account used to connect with the database.
     * @param password the password of the account.
     * @return a ConnectorString with given values for MySQL.
     */
    public static ConnectorString setCustomLocalSQLConnection(String databaseName, int port, String user, String password) {
        return new ConnectorString("localhost", port, databaseName, user, password);
    }

    /**
     * Connect to a local MongoDB server.
     * @param databaseName name of the database to connect to.
     * @param port the port where this connection is located.
     * @param user the username of the account used to connect with the database.
     * @param password the password of the account.
     * @return a ConnectorString with given values for MongoDB.
     */
    public static ConnectorString setCustomLocalMongoConnection(String databaseName, int port, String user, String password) {
        return new ConnectorString("localhost", port, databaseName, user, password, null);
    }

    /**
     * Connect to a local MongoDB server.
     * @param databaseName name of the database to connect to.
     * @param port the port where this connection is located.
     * @param user the username of the account used to connect with the database.
     * @param password the password of the account.
     * @param replicaSet the replica set use for this instance.
     * @return a ConnectorString with given values for MongoDB.
     */
    public static ConnectorString setCustomLocalMongoConnection(String databaseName, int port, String user, String password, String replicaSet) {
        return new ConnectorString("localhost", port, databaseName, user, password, replicaSet);
    }

    /**
     * Connect to a MySQL server.
     * @param host the address of the database server.
     * @param databaseName the name of the database to connect to.
     * @param port the port where this connection is located.
     * @param user the username of the account used to connect with the database.
     * @param password the password of the account.
     * @return a ConnectorString with given values for MySQL.
     */
    public static ConnectorString setSQLConnection(String host, String databaseName, int port, String user, String password) {
        return new ConnectorString(host, port, databaseName, user, password);
    }

    /**
     * Connect to a MongoDB server.
     * @param host the address of the database server.
     * @param databaseName the name of the database to connect to.
     * @param port the port where this connection is located.
     * @param user the username of the account used to connect with the database.
     * @param password the password of the account.
     * @return a ConnectorString with given values for MongoDB.
     */
    public static ConnectorString setMongoConnection(String host, String databaseName, int port, String user, String password) {
        return new ConnectorString(host, port, databaseName, user, password, null);
    }

    /**
     * Connect to a MongoDB server.
     * @param host the address of the database server.
     * @param databaseName the name of the database to connect to.
     * @param port the port where this connection is located.
     * @param user the username of the account used to connect with the database.
     * @param password the password of the account.
     * @param replicaSet the replica set to use for this instance.
     * @return a ConnectorString with given values for MongoDB.
     */
    public static ConnectorString setMongoConnection(String host, String databaseName, int port, String user, String password, String replicaSet) {
        return new ConnectorString(host, port, databaseName, user, password, replicaSet);
    }
}
