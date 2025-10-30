package dbConnect.models.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: finish document later
/**
 * This annotation marks a field to be stored as JSON in the database.
 * <p>
 * <b>Usage example:</b><br>
 * <pre>
 * {@code}
 * </pre>
 * @since 2.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonField {
    /**
     * If null values during serialization should be ignored.
     * @return true to ignore nulls, false to include
     */
    boolean ignoreNulls() default false;

    /**
     * If the data in the database should be stored as JSON string.
     * @return true to store as string, false to use Database's native
     */
    boolean storeAsString() default false;
}
