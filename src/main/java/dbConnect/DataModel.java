package dbConnect;


import dbConnect.mapper.DocumentInterface;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.models.enums.Collection;
import dbConnect.models.enums.Table;

public abstract class DataModel<T> {
    /**
     * A method uses to get the Table's name of the Data model Class.
     * @return {@link Table} enum assigned to this class.
     */
    public abstract Table getTable();

    /**
     * A method uses to get the Collection's name of the Data model CLass.
     * @return {@link Collection} enum assigned to this class.
     */
    public abstract Collection getCollection();

    /**
     * A method uses to return the Data model mapper method.
     * This one is for SQL {@link java.sql.ResultSet}.
     * @return The SQL mapper method of this class.
     */
    public abstract ResultSetInterface<T> getTableMap();

    /**
     * A method uses to return the Data model mapper method.
     * This one is for NoSQL {@link org.bson.Document}.
     * @return The NoSQL mapper method of this class.
     */
    public abstract DocumentInterface<T> getCollectionMap();
}
