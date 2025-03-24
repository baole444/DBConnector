package dbConnect;


import dbConnect.mapper.DocumentInterface;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.models.enums.Collection;
import dbConnect.models.enums.Table;

public abstract class DataModel<T> {
    /**
     *
     * @return Table enum assigned to this class
     */
    public abstract Table getTable();

    public abstract Collection getCollection();

    public abstract ResultSetInterface<T> getTableMap();

    public abstract DocumentInterface<T> getCollectionMap();
}
