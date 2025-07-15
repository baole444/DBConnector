package dbConnect.models.relationship;

import dbConnect.models.enums.CascadeType;
import dbConnect.models.enums.FetchMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks a field to have many-to-many relationship
 * with another model.<br>
 * The field should be of the target model's type.
 * <p>
 * <b>Usage example:</b><br>
 * <pre>
 * {@code
 * @TableName("order")
 * public class Order extends DataModel<Order> {
 *      // Others attributes
 *
 *      // Many orders may point to one customer.
 *      @ManyToOne(targetModel = Customer.class)
 *      @ForeignKey(column = "customer_id")
 *      private Customer customer;
 * }
 * }
 * </pre>
 * @since 2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToOne {
    /**
     * The target model class of the relationship.
     * @return the target data model class.
     */
    Class<?> targetModel();

    /**
     * The fetch strategy of this relationship.
     * Defaulted of {@code LAZY} if not specified.
     * @return the fetch method (LAZY or EAGER).
     */
    FetchMethod fetch() default FetchMethod.LAZY;

    /**
     * Control type of operations to be cascaded to the related model.
     * @return array of the cascade types.
     */
    CascadeType[] cascade() default {};
}
