package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Supplier {
    @AutomaticField @PrimaryField
    private String sup_id;

    private String sup_name;
    private String sup_address;
    private String sup_contact;
    private String sup_hotline;
    private String sup_note;

    public Supplier() {}

    public Supplier(String supname) {
        this.sup_name = supname;
    }

    public Supplier(String supname, String supaddress, String supcontact, String suphotline) {
        this.sup_name = supname;
        this.sup_address = supaddress;
        this.sup_contact = supcontact;
        this.sup_hotline = suphotline;
    }

    public Supplier(String supname, String supaddress, String supcontact, String suphotline, String supnote) {
        this.sup_name = supname;
        this.sup_address = supaddress;
        this.sup_contact = supcontact;
        this.sup_hotline = suphotline;
        this.sup_note = supnote;
    }

    public Supplier(String supid, String supname, String supaddress, String supcontact, String suphotline, String supnote) {
        this.sup_id = supid;
        this.sup_name = supname;
        this.sup_address = supaddress;
        this.sup_contact = supcontact;
        this.sup_hotline = suphotline;
        this.sup_note = supnote;
    }

    public String getSup_id() {
        return sup_id;
    }

    public void setSup_id(String sup_id) {
        this.sup_id = sup_id;
    }

    public String getSup_name() {
        return sup_name;
    }

    public void setSup_name(String sup_name) {
        this.sup_name = sup_name;
    }

    public String getSup_address() {
        return sup_address;
    }

    public void setSup_address(String sup_address) {
        this.sup_address = sup_address;
    }

    public String getSup_contact() {
        return sup_contact;
    }

    public void setSup_contact(String sup_contact) {
        this.sup_contact = sup_contact;
    }

    public String getSup_hotline() {
        return sup_hotline;
    }

    public void setSup_hotline(String sup_hotline) {
        this.sup_hotline = sup_hotline;
    }

    public String getSup_note() {
        return sup_note;
    }

    public void setSup_note(String sup_note) {
        this.sup_note = sup_note;
    }
    /*
    @Override
    public String toString() {
        return "Supplier ID: " + sup_id +
                "\nSupplier name: " + sup_name +
                "\nSupplier address: " + sup_address +
                "\nSupplier contact: " + sup_contact +
                "\nSupplier hotline: " + sup_hotline +
                "\nNote: " + sup_note;
    }
    */

    @Override
    public String toString() {
        return this.sup_name;
    }

    public static class SupplierMap implements DBMapper<Supplier> {
        @Override
        public Supplier map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("sup_id");
            String name = resultSet.getString("sup_name");
            String address = resultSet.getString("sup_address");
            String contact = resultSet.getString("sup_contact");
            String hotline = resultSet.getString("sup_hotline");
            String note = resultSet.getString("sup_note");
            return new Supplier(id, name, address, contact, hotline, note);
        }
    }

    public static Table getTable() {
        return Table.Supplier;
    }

    public static DBMapper<Supplier> getMap() {
        return new SupplierMap();
    }
}
