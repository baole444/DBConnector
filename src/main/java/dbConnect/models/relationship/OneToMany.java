package dbConnect.models.relationship;

import dbConnect.models.enums.CascadeType;
import dbConnect.models.enums.FetchMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks a field to have one-to-many relationship
 * with another model.<br>
 * The field should be a {@code Collection} (List, Set, etc.) of the target model's type.
 * <p>
 * <b>Usage example:</b><br>
 * <pre>
 * {@code
 * @TableName("customer")
 * public class Customer extends DataModel<Customer> {
 *      // Others attributes
 *
 *      // A customer may have many orders.
 *      @OneToMany(targetModel = Order.class, mappedBy = "customer_id")
 *      private List<Order> orders;
 * }
 * }
 * </pre>
 * @since 2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    /**
     * The target model class of the relationship.
     * @return the target data model class.
     */
    Class<?> targetModel();

    /**
     * The field of the targeted model that is used to map back to this model.<br>
     * For SQL: the foreign key column name in the target table.<br>
     * For MongoDB: the field name in the target collection.
     * @return name of the field that is used for mapping.
     */
    String mappedBy();

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
