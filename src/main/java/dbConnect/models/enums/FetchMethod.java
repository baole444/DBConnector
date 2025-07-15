package dbConnect.models.enums;

/**
 * Enum that defines the strategies for fetching data from the database.
 *
 * @since 2.2
 */
public enum FetchMethod {
    /**
     * Get related data when the parent entity is loaded.
     */
    EAGER,

    /**
     * Get related data on first access only.
     */
    LAZY
}
