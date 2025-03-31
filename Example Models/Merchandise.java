package dbConnect.models;

import com.mongodb.MongoException;
import dbConnect.DataModel;
import dbConnect.mapper.*;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.enums.Collection;
import dbConnect.models.enums.Table;
import dbConnect.models.notnull.NotNullField;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Merchandise extends DataModel<Merchandise> {
    @AutomaticField @PrimaryField @MaxLength(36)
    private String merch_id;

    @MongoOnly @AutomaticField
    private ObjectId _id;

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

    public Merchandise(ObjectId id, String merchname, float merchimportcost, float merchretailprice, float merch_taxrate, int merchcatid, String supid) {
        this._id = id;
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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
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

    public static class MerchandiseSQLMap implements ResultSetInterface<Merchandise> {
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

    public static class MerchandiseMongoMap implements DocumentInterface<Merchandise> {
        @Override
        public Merchandise map(Document document) throws MongoException {
            ObjectId id = document.getObjectId("_id");
            String name = document.getString("merch_name");
            float importCost = document.getDouble("merch_import_cost").floatValue();
            float retailPrice = document.getDouble("merch_retail_price").floatValue();
            float tax = document.getDouble("merch_taxrate").floatValue();
            int merchId = document.getInteger("merch_cat_id");
            String supplierId = document.getString("sup_id");
            return new Merchandise(id, name, importCost, retailPrice, tax, merchId, supplierId);
        }
    }

    @Override
    public Table getTable() {
        return Table.Merch;
    }

    @Override
    public Collection getCollection() {
        return Collection.Merch;
    }

    @Override
    public ResultSetInterface<Merchandise> getTableMap() {
        return new MerchandiseSQLMap();
    }

    @Override
    public DocumentInterface<Merchandise> getCollectionMap() {
        return new MerchandiseMongoMap();
    }
}
