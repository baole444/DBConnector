package example.model;

import dbConnect.DataModel;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.json.JsonField;
import dbConnect.models.meta.CollectionName;
import dbConnect.models.meta.TableName;
import dbConnect.models.notnull.NotNullField;
import dbConnect.models.relationship.ForeignKey;
import dbConnect.models.relationship.ManyToOne;

import java.util.Date;
import java.util.List;
import java.util.Map;

@TableName("product")
@CollectionName("product")
public class Product extends DataModel<Product> {
    @PrimaryField @NotNullField @MaxLength(10)
    private String productCode;

    @MaxLength @NotNullField
    private String productName;

    @NotNullField
    private double importPrice;

    @NotNullField
    private double profitRate;

    private Date importDate;

    @NotNullField @MaxLength(10)
    private String companyCode;

    private int stockQuantity;

    @JsonField
    private Map<String, Object> specifications;

    @JsonField
    private List<String> categories;

    @MaxLength(100)
    private String warranty;

    @JsonField
    private List<String> colorsAvailable;

    private boolean isBusinessModel;

    @JsonField
    private List<String> accessories;

    @JsonField
    private List<String> features;

    @ManyToOne(targetModel = Company.class)
    @ForeignKey(column = "companyCode", referencedColumnName = "companyCode")
    private transient Company company;

    public Product() {}

    public Product(String productCode, String productName, double importPrice, double profitRate, String companyCode) {
        this.productCode = productCode;
        this.productName = productName;
        this.importPrice = importPrice;
        this.profitRate = profitRate;
        this.companyCode = companyCode;
    }

    public Product(String productCode, String productName, double importPrice, double profitRate, Date importDate, String companyCode, int stockQuantity, Map<String, Object> specifications) {
        this.productCode = productCode;
        this.productName = productName;
        this.importPrice = importPrice;
        this.profitRate = profitRate;
        this.importDate = importDate;
        this.companyCode = companyCode;
        this.stockQuantity = stockQuantity;
        this.specifications = specifications;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }

    public double getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(double profitRate) {
        this.profitRate = profitRate;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Map<String, Object> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Map<String, Object> specifications) {
        this.specifications = specifications;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public List<String> getColorsAvailable() {
        return colorsAvailable;
    }

    public void setColorsAvailable(List<String> colorsAvailable) {
        this.colorsAvailable = colorsAvailable;
    }

    public boolean isBusinessModel() {
        return isBusinessModel;
    }

    public void setBusinessModel(boolean businessModel) {
        isBusinessModel = businessModel;
    }

    public List<String> getAccessories() {
        return accessories;
    }

    public void setAccessories(List<String> accessories) {
        this.accessories = accessories;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Double getSellingPrice() {
        return importPrice * profitRate;
    }

    @Override
    public String toString() {
        return productCode + " - " + productName;
    }
}
