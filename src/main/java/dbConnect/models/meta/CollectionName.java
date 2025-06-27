package dbConnect.models.meta;

import dbConnect.DataModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Assign declared collection's name from the database to the data model.
 * <p>
 * This annotation will take priority over {@link dbConnect.models.enums.Collection} Enum and {@link DataModel#getCollection()} method.
 * <p>
 * <b>Usage example:</b><br>
 * <i>Note: This annotation is not in conflict with {@link TableName}</i>
 * <pre>
 * {@code
 * @CollectionName("your_collection_name")
 * public class CustomModel extends DataModel<CustomModel> {
 *     // Existing methods
 * }
 * }
 * </pre>
 * @since 2.1
 * @see TableName Declaring table's name with annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CollectionName {
    /**
     * Get the collection's name.
     * @return name of the collection that is canonical to the database.
     */
    String value();
}
