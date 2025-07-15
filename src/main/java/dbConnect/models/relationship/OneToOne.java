package dbConnect.models.relationship;

import dbConnect.models.enums.CascadeType;
import dbConnect.models.enums.FetchMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface marks a field to have one-to-one relationship
 * with another model.<br>
 * <p>
 * <b>Usage example:</b><br>
 * <pre>
 * {@code
 * @TableName("customer")
 * public class Customer extends DataModel<Customer> {
 *      // Others attributes
 *
 *      // A customer may have one profile only.
 *      @OneToOne(targetModel = UserProfile.class)
 *      @ForeignKey(column = "profile_id")
 *      private UserProfile profile;
 * }
 * }
 * </pre>
 * @since 2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
    /**
     * The target model class of the relationship.
     * @return the target data model class.
     */
    Class<?> targetModel();

    /**
     * The name of the field of the targeted model to mapped back to this model.
     * This is for bidirectional relationship.
     * @return the name of the field, empty if unidirectional.
     */
    String mappedBy() default "";

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
