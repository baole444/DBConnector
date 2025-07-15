package dbConnect.models.enums;

/**
 * Enum that defines the set of cascadable operations
 * that is applicable to a relationship.
 *
 * @since 2.2
 */
public enum CascadeType {
    /**
     * Cascade all operations.
     */
    ALL,

    /**
     * Cascade persist operation.
     */
    PERSIST,

    /**
     * Cascade merge operation.
     */
    MERGE,

    /**
     * Cascade remove operation.
     */
    REMOVE,

    /**
     * Cascade refresh operation.
     */
    REFRESH,
}
