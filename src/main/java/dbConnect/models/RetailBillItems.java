package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RetailBillItems {
    @AutomaticField @PrimaryField
    private String rbi_id;

    private String rb_id;
    private String merch_id;
    private int quantity;
    private float price;

    public RetailBillItems() {}

    public RetailBillItems(String rbid, String merchid) {
        this.rb_id = rbid;
        this.merch_id = merchid;
        this.quantity = 1;
        this.price = 0.00f;
    }

    public RetailBillItems(String rbid, String merchid, int quantity, float price) {
        this.rb_id = rbid;
        this.merch_id = merchid;
        this.quantity = quantity;
        this.price = price;
    }

    public RetailBillItems(String rbiid, String rbid, String merchid, int quantity, float price) {
        this.rbi_id = rbiid;
        this.rb_id = rbid;
        this.merch_id = merchid;
        this.quantity = quantity;
        this.price = price;
    }

    public String getRbi_id() {
        return rbi_id;
    }

    public void setRbi_id(String rbi_id) {
        this.rbi_id = rbi_id;
    }

    public String getRb_id() {
        return rb_id;
    }

    public void setRb_id(String rb_id) {
        this.rb_id = rb_id;
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

    @Override
    public String toString() {
        return "Item ID: " + rbi_id +
                "\nImport bill ID: " + rb_id +
                "\nMerch ID: " + merch_id +
                "\nAmount: " + quantity +
                "\nPrice: " + price;
    }

    public static class RetailBillItemsMap implements DBMapper<RetailBillItems> {
        @Override
        public RetailBillItems map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("rbi_id");
            String retailBillId = resultSet.getString("rb_id");
            String merchId = resultSet.getString("merch_id");
            int quantity = resultSet.getInt("quantity");
            float price = resultSet.getFloat("price");
            return new RetailBillItems(id, retailBillId, merchId, quantity, price);
        }
    }

    public static Table getTable() {
        return Table.RetailItem;
    }

    public static DBMapper<RetailBillItems> getMap() {
        return new RetailBillItemsMap();
    }
}
