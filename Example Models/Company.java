package example.model;

import dbConnect.DataModel;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.json.JsonField;
import dbConnect.models.meta.CollectionName;
import dbConnect.models.meta.TableName;
import dbConnect.models.notnull.NotNullField;

import java.util.List;
import java.util.Map;

@TableName("company")
@CollectionName("company")
public class Company extends DataModel<Company> {
    @PrimaryField @NotNullField @MaxLength(10)
    private String companyCode;

    @NotNullField @MaxLength
    private String companyName;

    @JsonField
    private Map<String, Object> address;

    @JsonField
    private Map<String, Object> contact;

    @MaxLength(50)
    private String taxCode;

    private int establishedYear;

    @MaxLength(510)
    private String website;

    @JsonField
    private List<String> businessType;

    private boolean isPublic;

    @MaxLength(100)
    private String revenue;

    @MaxLength(100)
    private String marketCap;

    public Company() {}

    public Company(String companyCode, String companyName) {
        this.companyCode = companyCode;
        this.companyName = companyName;
    }

    public Company(String companyCode, String companyName, Map<String, Object> address, Map<String, Object> contact, String taxCode, int establishedYear) {
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.address = address;
        this.contact = contact;
        this.taxCode = taxCode;
        this.establishedYear = establishedYear;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Map<String, Object> getAddress() {
        return address;
    }

    public void setAddress(Map<String, Object> address) {
        this.address = address;
    }

    public Map<String, Object> getContact() {
        return contact;
    }

    public void setContact(Map<String, Object> contact) {
        this.contact = contact;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public int getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(int establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getBusinessType() {
        return businessType;
    }

    public void setBusinessType(List<String> businessType) {
        this.businessType = businessType;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    @Override
    public String toString() {
        return companyCode + " - " + companyName;
    }
}
