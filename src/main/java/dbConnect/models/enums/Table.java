package dbConnect.models.enums;

/**
 * Enums responsible for parsing table's name.
 * @deprecated since 2.1 - replaced by {@link dbConnect.models.meta.TableName} annotation.
 */
@Deprecated(since = "2.1")
public enum Table {
    /**
     * Generic table
     */
    Generic("generic");

    private final String TableName;

    /**
     * Table constructor. Used to assign {@code TableName} instance.
     * @param tableName corresponding {@code TableName} of each enum.
     */
    Table(String tableName) {
        this.TableName = tableName;
    }

    /**
     * An internal method to fetch {@code TableName} of each enum.
     *
     * @return TableName of the enum, formatted as {@link String}.
     */
    public String getName() {
        return TableName;
    }
}
