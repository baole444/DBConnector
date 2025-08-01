package dbConnect.models.relationship;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a field as a point to
 * store reference of the desired relationship.<br>
 * The field should be of the target model's type.
 * <p>
 * <b>Usage example:</b><br>
 * <pre>
 * {@code
 * @TableName("order")
 * public class Order extends DataModel<Order> {
 *      // Others attributes
 *
 *      // The related model Customer in Order model,
 *      // where customer_id is the foreign key.
 *      @ForeignKey(column = "customer_id")
 *      private Customer customer;
 * }
 * }
 * </pre>
 * @since 2.5
 */
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
