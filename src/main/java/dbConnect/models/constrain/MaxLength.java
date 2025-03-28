package dbConnect.models.constrain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks an attribute's maximum length to user defined value or defaults to {@code 255}.<br>
 * {@link dbConnect.execution.InsertParser} and {@link dbConnect.execution.UpdateParser} will enforce all fields value with this notation to be limited by the defined value.<br>
 * If an attribute already has {@link dbConnect.models.autogen.AutomaticField} annotation, it will take priority over this annotation.<br>
 * Example usage:
 * <pre><code>
 * {@literal @}MaxLength
 *  String CustomerName;
 *
 * {@literal @}MaxLength(36)
 *  String StoreID;
 * </code></pre>
 * @since version 1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MaxLength {
    int value() default 255;
}
