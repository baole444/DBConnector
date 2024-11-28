package dbConnect.models;

import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MerchCategory {
    @AutomaticField @PrimaryField
    private int merch_cat_id;

    @MaxLength(100)
    private String merch_cat_name;
    private float merch_cat_taxrate;

    public MerchCategory() {}

    public MerchCategory(String merchcatname) {
        this.merch_cat_name = merchcatname;
        this.merch_cat_taxrate = 0.00f;
    }

    public MerchCategory(String merchcatname, float merchcattaxrate) {
        this.merch_cat_name = merchcatname;
        this.merch_cat_taxrate = merchcattaxrate;
    }

    public MerchCategory(int merchcatid, String merchcatname, float merchcattaxrate) {
        this.merch_cat_id = merchcatid;
        this.merch_cat_name = merchcatname;
        this.merch_cat_taxrate = merchcattaxrate;
    }
    /*
    @Override
    public String toString() {
        return "Merch category ID: " + merch_cat_id +
                "\nMerch category name: " + merch_cat_name +
                "\nMerch category tax rate: " + merch_cat_taxrate;
    }
     */

    @Override
    public String toString() {
        return this.merch_cat_name;
    }

    public int getMerch_cat_id() {
        return merch_cat_id;
    }

    public void setMerch_cat_id(int merch_cat_id) {
        this.merch_cat_id = merch_cat_id;
    }

    public String getMerch_cat_name() {
        return merch_cat_name;
    }

    public void setMerch_cat_name(String merch_cat_name) {
        this.merch_cat_name = merch_cat_name;
    }

    public float getMerch_cat_taxrate() {
        return merch_cat_taxrate;
    }

    public void setMerch_cat_taxrate(float merch_cat_taxrate) {
        this.merch_cat_taxrate = merch_cat_taxrate;
    }

    public static class MerchCatMap implements DBMapper<MerchCategory> {
        @Override
        public MerchCategory map(ResultSet resultSet) throws SQLException {
            int id = resultSet.getInt("merch_cat_id");
            String name = resultSet.getString("merch_cat_name");
            float tax = resultSet.getFloat("merch_cat_taxrate");
            return new MerchCategory(id, name, tax);
        }
    }

    public static Table getTable() {
        return Table.MerchCategory;
    }

    public static DBMapper<MerchCategory> getMap() {
        return new MerchCatMap();
    }
}
