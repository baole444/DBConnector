package dbConnect.models.constrain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface limits an attribute to only be available in MySQL scope.<br>
 * Example usage:
 * <pre><code>
 * {@literal @}MySQLOnly
 *  String table_id;
 * </code></pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MySQLOnly {
}
