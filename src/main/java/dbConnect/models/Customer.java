package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer {
    @AutomaticField @PrimaryField
    private String cus_id;

    private String cus_firstname;
    private String cus_lastname;
    private String cus_phone;
    private String cus_address;
    private String cus_note;

    public Customer() {}

    public Customer(String cusfirstname, String cuslastname, String cusphone, String cusaddress) {
        this.cus_firstname = cusfirstname;
        this.cus_lastname = cuslastname;
        this.cus_phone = cusphone;
        this.cus_address = cusaddress;
    }

    public Customer(String cusfirstname, String cuslastname, String cusphone, String cusaddress, String cusnote) {
        this.cus_firstname = cusfirstname;
        this.cus_lastname = cuslastname;
        this.cus_phone = cusphone;
        this.cus_address = cusaddress;
        this.cus_note = cusnote;
    }
    public Customer(String cusid, String cusfirstname, String cuslastname, String cusphone, String cusaddress, String cusnote) {
        this.cus_id = cusid;
        this.cus_firstname = cusfirstname;
        this.cus_lastname = cuslastname;
        this.cus_phone = cusphone;
        this.cus_address = cusaddress;
        this.cus_note = cusnote;
    }

    public String getCus_id() {
        return cus_id;
    }

    public void setCus_id(String cus_id) {
        this.cus_id = cus_id;
    }

    public String getCus_firstname() {
        return cus_firstname;
    }

    public void setCus_firstname(String cus_firstname) {
        this.cus_firstname = cus_firstname;
    }

    public String getCus_lastname() {
        return cus_lastname;
    }

    public void setCus_lastname(String cus_lastname) {
        this.cus_lastname = cus_lastname;
    }

    public String getCus_phone() {
        return cus_phone;
    }

    public void setCus_phone(String cus_phone) {
        this.cus_phone = cus_phone;
    }

    public String getCus_address() {
        return cus_address;
    }

    public void setCus_address(String cus_address) {
        this.cus_address = cus_address;
    }

    public String getCus_note() {
        return cus_note;
    }

    public void setCus_note(String cus_note) {
        this.cus_note = cus_note;
    }

    @Override
    public String toString() {
        return this.cus_firstname + this.cus_lastname;
    }

    public static class CustomerMap implements DBMapper<Customer> {
        @Override
        public Customer map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("cus_id");
            String firstName = resultSet.getString("cus_firstname");
            String lastname = resultSet.getString("cus_lastname");
            String phone = resultSet.getString("cus_phone");
            String address = resultSet.getString("cus_address");
            String note = resultSet.getString("cus_note");
            return new Customer(id, firstName, lastname, phone, address, note);
        }
    }

    public static Table getTable() {
        return Table.Customer;
    }

    public static DBMapper<Customer> getMap() {
        return new CustomerMap();
    }
}
