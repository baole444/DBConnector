package dbConnect.models.enums;

public enum Table {
    Supplier("supplier"),
    MerchCategory("merch_category"),
    Merch("merchandise"),
    Customer("customer"),
    ImportBill("import_bill"),
    ImportItem("import_bill_items"),
    RetailBill("retail_bill"),
    RetailItem("retail_bill_items"),
    Generic("generic");

    private final String TableName;

    Table(String tableName) {
        this.TableName = tableName;
    }

    public String getName() {
        return TableName;
    }
}
