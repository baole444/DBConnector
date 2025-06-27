package dbConnect;


import dbConnect.mapper.DocumentInterface;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.models.enums.Collection;
import dbConnect.models.enums.Table;
import dbConnect.models.meta.TableName;
import dbConnect.models.meta.CollectionName;

/**
 * An abstraction to provide standard for data models.
 * <p>
 * <b>Example Usage:</b><br>
 * <b> * <u>Caution</u>:</b> the empty constructor is a requirement for various parsing methods.
 * <pre>
 * {@code
 * public class CustomDataModel extends DataModel<CustomDataModel> {
 *     public CustomDataModel() {}
 *
 *     // Other attributes and methods.
 * }
 * }
 * </pre>
 * @see TableName Declare table name for data model
 * @see CollectionName Declare collection name for data model
 * @param <T> The class (data model) to be extended to.
 */
public abstract class DataModel<T> {
    /**
     * Get the table's name for the model.
     * This required at least annotation for the Class using {@link TableName} or fallback to old {@code getTable()} method.
     * @return Name of the table that match the Database declared table.
     *
     * @since 2.1
     */
    public final String getTableName() {
        Class<?> c = this.getClass();
        TableName annotation = c.getAnnotation(TableName.class);
        if (annotation != null) {
            return annotation.value();
        }

        try {
            return getTable().getName();
        } catch (Exception e) {
            throw new IllegalStateException("Model is required to have at least TableName annotation or getTable method");
        }
    }

    /**
     * Get the collection's name for the model.
     * This required at least annotation for the Class using {@link CollectionName} or fallback to old {@code getCollection()} method.
     * @return Name of the table that match the Database declared table.
     *
     * @since 2.1
     */
    public final String getCollectionName() {
        Class<?> c = this.getClass();
        CollectionName annotation = c.getAnnotation(CollectionName.class);
        if (annotation != null) {
            return annotation.value();
        }

        try {
            return getCollection().getName();
        } catch (Exception e) {
            throw new IllegalStateException("Model is required to have at least CollectionName annotation or getCollection method");
        }
    }

    /**
     * A method used to get the Table's name of the Data model Class.
     * @return {@link Table} enum assigned to this class.
     * @deprecated since 2.1 - Use {@code TableName} annotation instead.
     * @see TableName
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public Table getTable() {
        return null;
    }

    /**
     * A method used to get the Collection's name of the Data model CLass.
     * @return {@link Collection} enum assigned to this class.
     * @deprecated since 2.1 - Use {@code CollectionName} annotation instead.
     * @see CollectionName
     */
    @Deprecated(since = "2.1", forRemoval = true)
    public Collection getCollection() {
        return null;
    }

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
