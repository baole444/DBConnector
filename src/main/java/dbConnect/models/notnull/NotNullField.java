package dbConnect.models.notnull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks an attribute to be not null by the database.<br>
 * Parser will enforce all fields with this notation to be initialized.<br>
 * If an attribute already has {@link dbConnect.models.autogen.AutomaticField} annotation, it will take priority over this annotation.
 *
 * @since version 1.5
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNullField {
}
