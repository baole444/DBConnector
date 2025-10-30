package dbConnect.models.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks an attribute to be the primary key of a model.<br>
 * <i>* There should only be one field borne primary notation existed at the same time in a model.</i>
 * <p>
 * <b>Usage example:</b>
 * <pre>
 * {@code
 *      @PrimaryField
 *      String CustomerID;
 * }
 * </pre>
 * @since 2.5
 * <p>
 * <b>New feature:</b>
 * <i> You can now specify if the primary key is for MongoDB, MySQL, or both!</i>
 * <pre>
 * {@code
 *      @PrimaryField(forMongo = false)
 *      int sqlAutoIncrement;
 *
 *      @PrimaryField(forSQL = false)
 *      String customUUID;
 *
 *      // Default for both is true so explicit true is not required
 *      @PrimaryField(forSQL = true, forMongo = true)
 *      String uniqueID;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryField {
    /**
     * If this field should be used as primary key for SQL.
     * @return true to use as SQL primary key (default behaviour).
     */
    boolean forSQL() default true;

    /**
     * If this field should be used as primary key for MongoDB.
     * This will take priority over {@code ObjectId _id}.
     * @return true to use as MongoDB primary key (default behaviour).
     */
    boolean forMongo() default true;
}
