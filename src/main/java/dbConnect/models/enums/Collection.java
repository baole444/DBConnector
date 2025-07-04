package dbConnect.models.enums;

/**
 * Enums responsible for parsing collection's name.
 * @deprecated since 2.1, replaced by {@link dbConnect.models.meta.CollectionName} annotation.
 */
@Deprecated(since = "2.1")
public enum Collection {
    /**
     * Generic collection
     */
    Generic("generic");

    private final String CollectionName;

    /**
     * Collection constructor. Used to assign {@code CollectionName} instance.
     * @param collectionName corresponding {@code CollectionName} of each enum.
     */
    Collection(String collectionName) {
        this.CollectionName = collectionName;
    }

    /**
     * An internal method to fetch {@code CollectionName} of each enum.
     *
     * @return CollectionName of the enum, formatted as {@link String}.
     */
    public String getName() {
        return CollectionName;
    }
}
