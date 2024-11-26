package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class RetailBill {
    @AutomaticField @PrimaryField
    private String rb_id;

    private boolean rb_payment_status;

    @AutomaticField
    private Timestamp rb_create_date;

    private String cus_id;
    private double paid;

    public RetailBill() {}

    public RetailBill(boolean rbpaymentstatus, String cusid, double paid) {
        this.rb_payment_status = rbpaymentstatus;
        this.cus_id = cusid;
        this.paid = paid;
    }

    public RetailBill(String rbid, boolean rbpaymentstatus, Timestamp rbcreatedate, String cusid, double paid) {
        this.rb_id = rbid;
        this.rb_payment_status = rbpaymentstatus;
        this.rb_create_date = rbcreatedate;
        this.cus_id = cusid;
        this.paid = paid;
    }

    public String getRb_id() {
        return rb_id;
    }

    public void setRb_id(String rb_id) {
        this.rb_id = rb_id;
    }

    public boolean isRb_payment_status() {
        return rb_payment_status;
    }

    public void setRb_payment_status(boolean rb_payment_status) {
        this.rb_payment_status = rb_payment_status;
    }

    public Timestamp getRb_create_date() {
        return rb_create_date;
    }

    public void setRb_create_date(Timestamp rb_create_date) {
        this.rb_create_date = rb_create_date;
    }

    public String getCus_id() {
        return cus_id;
    }

    public void setCus_id(String cus_id) {
        this.cus_id = cus_id;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public String statusParse(boolean payStatus) {
        if (payStatus) {
            return "Payment concluded.";
        } else {
            return "Payment not done.";
        }
    }

    @Override
    public String toString() {
        return "Retail bill Id: " + rb_id +
                "\nPayment status: " + statusParse(this.rb_payment_status) +
                "\nCreate time: " + rb_create_date +
                "\nCustomer Id: " + cus_id +
                "\nAmount paid: " + paid;
    }

    public static class RetailBillMap implements DBMapper<RetailBill> {
        @Override
        public RetailBill map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("rb_id");
            boolean payStatus = resultSet.getBoolean("rb_payment_status");
            Timestamp createdDate = resultSet.getTimestamp("rb_create_date");
            String customerId = resultSet.getString("cus_id");
            double paid = resultSet.getDouble("paid");
            return new RetailBill(id, payStatus, createdDate, customerId, paid);
        }
    }

    public static Table getTable() {
        return Table.RetailBill;
    }

    public static DBMapper<RetailBill> getMap() {
        return new RetailBillMap();
    }
}
