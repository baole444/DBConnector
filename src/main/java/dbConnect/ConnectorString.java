package dbConnect;

public class ConnectorString {
    private String host;
    private int port;
    private String databaseName;
    private String user;
    private String password;

    public ConnectorString(String host, int port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
    }

    public String getConnString() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public static ConnectorString defaultConn() {
        return new ConnectorString("localhost", 3306, "store_db", "root", "root");
    }

    public static ConnectorString setDefaultLocalConnection(String databaseName) {
        return new ConnectorString("localhost", 3306, databaseName, "root", "root");
    }

    public static ConnectorString setCustomLocalConnection(String databaseName, int port, String user, String password) {
        return new ConnectorString("localhost", port, databaseName, user, password);
    }

    public static ConnectorString setConnection(String host, String databaseName, int port, String user, String password) {
        return new ConnectorString(host, port, databaseName, user, password);

    }

}
