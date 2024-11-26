package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ImportBill {
    @AutomaticField @PrimaryField
    private String ib_id;

    private boolean ib_payment_status;

    @AutomaticField
    private Timestamp ib_create_date;

    private String sup_id;
    private double paid;

    public ImportBill() {}

    public ImportBill(boolean ibpaymentstatus, String supid, double paid) {
        this.ib_payment_status = ibpaymentstatus;
        this.sup_id = supid;
        this.paid = paid;
    }

    public ImportBill(String ibid, boolean ibpaymentstatus, Timestamp ibcreatedate, String supid, double paid) {
        this.ib_id = ibid;
        this.ib_payment_status = ibpaymentstatus;
        this.ib_create_date = ibcreatedate;
        this.sup_id = supid;
        this.paid = paid;
    }

    public String getIb_id() {
        return ib_id;
    }

    public void setIb_id(String ib_id) {
        this.ib_id = ib_id;
    }

    public boolean isIb_payment_status() {
        return ib_payment_status;
    }

    public void setIb_payment_status(boolean ib_payment_status) {
        this.ib_payment_status = ib_payment_status;
    }

    public Timestamp getIb_create_date() {
        return ib_create_date;
    }

    public void setIb_create_date(Timestamp ib_create_date) {
        this.ib_create_date = ib_create_date;
    }

    public String getSup_id() {
        return sup_id;
    }

    public void setSup_id(String sup_id) {
        this.sup_id = sup_id;
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
        return "Import bill ID: " + ib_id +
                "\nPayment status: " + statusParse(this.ib_payment_status) +
                "\nCreate time: " + ib_create_date +
                "\nSupplier ID: " + sup_id +
                "\nAmount paid: " + paid;
    }

    public static class ImportBillMap implements DBMapper<ImportBill> {
        @Override
        public ImportBill map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("ib_id");
            boolean payStatus = resultSet.getBoolean("ib_payment_status");
            Timestamp createdDate = resultSet.getTimestamp("ib_create_date");
            String supplierId = resultSet.getString("sup_id");
            double paid = resultSet.getDouble("paid");
            return new ImportBill(id, payStatus, createdDate, supplierId, paid);
        }
    }

    public static Table getTable() {
        return Table.ImportBill;
    }

    public static DBMapper<ImportBill> getMap() {
        return new ImportBillMap();
    }
}
