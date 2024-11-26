package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImportBillItems {
    @AutomaticField @PrimaryField
    private String ibi_id;

    private String ib_id;
    private String merch_id;
    private int quantity;
    private float price;
    private float tax;

    public ImportBillItems() {}

    public ImportBillItems(String ibid, String merchid) {
        this.ib_id = ibid;
        this.merch_id = merchid;
        this.quantity = 1;
        this.price = 0.00f;
        this.tax = 0.00f;
    }

    public ImportBillItems(String ibid, String merchid, int quantity, float price, float tax) {
        this.ib_id = ibid;
        this.merch_id = merchid;
        this.quantity = quantity;
        this.price = price;
        this.tax = tax;
    }

    public ImportBillItems(String ibiid, String ibid, String merchid, int quantity, float price, float tax) {
        this.ibi_id = ibiid;
        this.ib_id = ibid;
        this.merch_id = merchid;
        this.quantity = quantity;
        this.price = price;
        this.tax = tax;
    }

    public String getIbi_id() {
        return ibi_id;
    }

    public void setIbi_id(String ibi_id) {
        this.ibi_id = ibi_id;
    }

    public String getIb_id() {
        return ib_id;
    }

    public void setIb_id(String ib_id) {
        this.ib_id = ib_id;
    }

    public String getMerch_id() {
        return merch_id;
    }

    public void setMerch_id(String merch_id) {
        this.merch_id = merch_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return "Item ID: " + ibi_id +
                "\nImport bill ID: " + ib_id +
                "\nMerch ID: " + merch_id +
                "\nAmount: " + quantity +
                "\nPrice: " + price +
                "\nTax rate: " + tax;
    }

    public static class ImportBillItemsMap implements DBMapper<ImportBillItems> {
        @Override
        public ImportBillItems map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("ibi_id");
            String importBillId = resultSet.getString("ib_id");
            String merchId = resultSet.getString("merch_id");
            int quantity = resultSet.getInt("quantity");
            float price = resultSet.getFloat("price");
            float tax = resultSet.getFloat("tax");
            return new ImportBillItems(id, importBillId, merchId, quantity, price, tax);
        }
    }

    public static Table getTable() {
        return Table.ImportItem;
    }

    public static DBMapper<ImportBillItems> getMap() {
        return new ImportBillItemsMap();
    }
}
