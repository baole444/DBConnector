package dbConnect.models.enums;

/**
 * Enums responsible for parsing table's name.
 */
public enum Table {
    MerchCategory("merch_category"),
    Merch("merchandise"),
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
