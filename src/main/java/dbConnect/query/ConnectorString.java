package dbConnect.query;

public class ConnectorString {
    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final String password;
    private final String replicaSet;


    public ConnectorString(String host, int port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.replicaSet = null;
    }

    public ConnectorString(String host, int port, String databaseName, String user, String password, String replicaSet) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
        this.replicaSet = replicaSet;
    }

    public String getSQLConnectionString() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
    }

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

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public static ConnectorString loadDefaultSQLConnection() {
        return new ConnectorString("localhost", 3306, "store_db", "root", "root");
    }

    public static ConnectorString loadDefaultMongoConnection() {
        return new ConnectorString("localhost", 27017, "store_db", null, null, null);
    }

    public static ConnectorString setDefaultLocalSQLConnection(String databaseName) {
        return new ConnectorString("localhost", 3306, databaseName, "root", "root");
    }

    public static ConnectorString setDefaultLocalMongoConnection(String databaseName) {
        return new ConnectorString("localhost", 27017, databaseName, null, null, null);
    }

    public static ConnectorString setCustomLocalSQLConnection(String databaseName, int port, String user, String password) {
        return new ConnectorString("localhost", port, databaseName, user, password);
    }

    public static ConnectorString setCustomLocalMongoConnection(String databaseName, int port, String user, String password) {
        return new ConnectorString("localhost", port, databaseName, user, password, null);
    }

    public static ConnectorString setCustomLocalMongoConnection(String databaseName, int port, String user, String password, String replicaSet) {
        return new ConnectorString("localhost", port, databaseName, user, password, replicaSet);
    }

    public static ConnectorString setSQLConnection(String host, String databaseName, int port, String user, String password) {
        return new ConnectorString(host, port, databaseName, user, password);
    }

    public static ConnectorString setMongoConnection(String host, String databaseName, int port, String user, String password) {
        return new ConnectorString(host, port, databaseName, user, password, null);
    }

    public static ConnectorString setMongoConnection(String host, String databaseName, int port, String user, String password, String replicaSet) {
        return new ConnectorString(host, port, databaseName, user, password, replicaSet);
    }
}
