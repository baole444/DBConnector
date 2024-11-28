package dbConnect.models.autogen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This interface marks an attribute to be managed by the database server.<br>
 * {@link dbConnect.execution.InsertParser} will skip all fields with this notation.<br>
 * Example usage:
 * <pre><code>
 * {@literal @}AutomaticField
 *  String CustomerID;
 * </code></pre>
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface AutomaticField {
}
