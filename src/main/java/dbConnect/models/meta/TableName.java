package dbConnect.models.meta;

import dbConnect.DataModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Assign declared table's name from the database to the data model.
 * <p>
 * This annotation will take priority over {@link dbConnect.models.enums.Table} Enum and {@link DataModel#getTable()} method.
 * <p>
 * <b>Usage example:</b><br>
 * <i>Note: This annotation is not in conflict with {@link CollectionName}</i>
 * <pre>
 * {@code
 * @TableName("your_table_name")
 * public class CustomModel extends DataModel<CustomModel> {
 *     // Existing methods
 * }
 * }
 * </pre>
 * @since 2.1
 * @see CollectionName Declaring collection's name with annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {
    /**
     * Get the table's name.
     * @return name of the table that is canonical to the database.
     */
    String value();
}
