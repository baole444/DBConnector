package dbConnect.models.relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
    /**
     * The name of the foreign key column.
     * @return the column's name.
     */
    String column();

    /**
     * The name of the column this foreign key is referencing.
     * Default to the primary key of the referenced data model.
     * @return the referenced's column's name.
     */
    String referencedColumnName() default "";

    /**
     * If the foreign key can be null.
     * @return true if nullable, false of otherwise.
     */
    boolean nullable() default true;
}
