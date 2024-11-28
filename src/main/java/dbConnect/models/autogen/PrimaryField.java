package dbConnect.models.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks an attribute to be the primary key of a model.<br>
 * <i><strong>There should only be one field borne primary notation existed at the same time in a model.</strong></i><br>
 * Example usage:
 * <pre><code>
 * {@literal @}PrimaryField
 *  String CustomerID;
 * </code></pre>
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryField {
}
