package dbConnect.models;

import dbConnect.mapper.DataMapper;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.mapper.SQLMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Merchandise {
    @AutomaticField @PrimaryField @MaxLength(36)
    private String merch_id;

    @NotNullField @MaxLength(100)
    private String merch_name;
    private float merch_import_cost;
    private float merch_retail_price;
    private float merch_taxrate;

    @MaxLength(36)
    private int merch_cat_id;

    @MaxLength(36)
    private String sup_id;

    public Merchandise() {}

    public Merchandise(String merchname, float merchimportcost, float merchretailprice, int merchcatid, String supid) {
        this.merch_name = merchname;
        this.merch_import_cost = merchimportcost;
        this.merch_retail_price = merchretailprice;
        this.merch_taxrate = 0.00f;
        this.merch_cat_id = merchcatid;
        this.sup_id = supid;
    }

    public Merchandise(String merchname, float merchimportcost, float merchretailprice, float merch_taxrate, int merchcatid, String supid) {
        this.merch_name = merchname;
        this.merch_import_cost = merchimportcost;
        this.merch_retail_price = merchretailprice;
        this.merch_taxrate = merch_taxrate;
        this.merch_cat_id = merchcatid;
        this.sup_id = supid;
    }

    public Merchandise(String merchid, String merchname, float merchimportcost, float merchretailprice, float merch_taxrate, int merchcatid, String supid) {
        this.merch_id = merchid;
        this.merch_name = merchname;
        this.merch_import_cost = merchimportcost;
        this.merch_retail_price = merchretailprice;
        this.merch_taxrate = merch_taxrate;
        this.merch_cat_id = merchcatid;
        this.sup_id = supid;
    }

    public String getMerch_id() {
        return merch_id;
    }

    public void setMerch_id(String merch_id) {
        this.merch_id = merch_id;
    }

    public String getMerch_name() {
        return merch_name;
    }

    public void setMerch_name(String merch_name) {
        this.merch_name = merch_name;
    }

    public float getMerch_import_cost() {
        return merch_import_cost;
    }

    public void setMerch_import_cost(float merch_import_cost) {
        this.merch_import_cost = merch_import_cost;
    }

    public float getMerch_retail_price() {
        return merch_retail_price;
    }

    public void setMerch_retail_price(float merch_retail_price) {
        this.merch_retail_price = merch_retail_price;
    }

    public float getMerch_taxrate() {
        return merch_taxrate;
    }

    public void setMerch_taxrate(float merch_taxrate) {
        this.merch_taxrate = merch_taxrate;
    }

    public int getMerch_cat_id() {
        return merch_cat_id;
    }

    public void setMerch_cat_id(int merch_cat_id) {
        this.merch_cat_id = merch_cat_id;
    }

    public String getSup_id() {
        return sup_id;
    }

    public void setSup_id(String sup_id) {
        this.sup_id = sup_id;
    }

    @Override
    public String toString() {
        return this.merch_name;
    }

    public static class MerchandiseMap implements ResultSetInterface<Merchandise> {
        @Override
        public Merchandise map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("merch_id");
            String name = resultSet.getString("merch_name");
            float importCost = resultSet.getFloat("merch_import_cost");
            float retailPrice = resultSet.getFloat("merch_retail_price");
            float tax = resultSet.getFloat("merch_taxrate");
            int merchId = resultSet.getInt("merch_cat_id");
            String supplierId = resultSet.getString("sup_id");
            return new Merchandise(id, name, importCost, retailPrice, tax, merchId, supplierId);
        }
    }

    public static Table getTable() {
        return Table.Merch;
    }

    public static ResultSetInterface<Merchandise> getMap() {
        return new MerchandiseMap();
    }
}
