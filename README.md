# MySQL Database Connector
An abstraction for JDBC data query tasks.

## Feature:
Parsing query statements base on data model.
Insert, Update, Delete, Retrieve data with ease.

## Installation:
This project uses Gradle (8.7) for easy import of dependencies and build tasks, make sure to add this to your `build.gradle`:
### build.gradle:
```
repositories {
    flatDir {
        dirs 'Lib'
    }
    mavenCentral()
}

dependencies {
    implementation fileTree('Lib') { include '*.jar' }
}
```
> [!IMPORTANT]   
> Enums used by the system (such as table name) is statically complied at runtime and cannot be added. 
> If you decided to compile the project and add it as a jar file, make sure to update necessary enums or built in data models that you decided.

Create a folder call `Lib` within your project and put the `DBConnect-<version>.jar` inside (You can skip this if you use the source code directly).<br>
Make sure to also put the `mysql-connector-j-<version>.jar` inside if you don't have it as your project dependencies yet.

### Create a data model Class (modelClass):
The current abstraction allows user to create their own data model following the supported structure.
With each attribute represent a column and its name<br>

Let's take a look at a MySql Script of a table and create your model from it.
#### Example table:
```sql
-- Example table for merchandise category
create table merch_category (
    merch_cat_id int not null auto_increment,
    merch_cat_name varchar(100) not null,
    merch_cat_taxrate decimal,
    primary key (merch_cat_id)
);
```

#### Annotation for an attribute:
Currently, the system allows three types of annotation to mark which field is managed by the database server, which is a primary key and which is not null.<br>
In the example, the `merch_cat_id` column is a primary key managed by server. So we need to tell the system what it is.
```java
    @AutomaticField @PrimaryField
    private int merch_cat_id;
```
If you want to constrain a column's value to be not null add `@NotNullField` annotation to it.
```java
    @NotNullField
    private String merch_cat_name;
```

> [!NOTE]   
> @AutomaticField will always take priority over @NotNullField

#### Table Enums class:
Current system store table's name in enums, make sure to update `dbConnect.models.enums.Table` to included your data model. At this point, you can also update `DataModel` enum if you decided to use it.

```java
package dbConnect.models.enums;

public enum Table {
    Supplier("supplier"),
    MerchCategory("merch_category"),
    Merch("merchandise"),
    Customer("customer"),
    ImportBill("import_bill"),
    ImportItem("import_bill_items"),
    RetailBill("retail_bill"),
    RetailItem("retail_bill_items"),
    Generic("generic"),
    // Add your table enums here
     ExampleEnums("example_table_name");
    
    // End code
    private final String TableName;

    Table(String tableName) {
        this.TableName = tableName;
    }

    public String getName() {
        return TableName;
    }
}
```

#### Create Merchandise Category Model:
Your data model should have some basic method to initialize it like constructor for the class, getters, and setters for attributes. However, there are three important required methods that you have to add:

##### Result Mapping:
Result mapping allows streamline call when retrieving data. This method implements `DBMapper` interface to parse the result from a query into your model class instance and return it.
```java
    public static class MerchCatMap implements DBMapper<MerchCategory> {
        @Override
        public MerchCategory map(ResultSet resultSet) throws SQLException {
            // binding result set to local variables from their respective column name.
            int id = resultSet.getInt("merch_cat_id");
            String name = resultSet.getString("merch_cat_name");
            float tax = resultSet.getFloat("merch_cat_taxrate");
            
            // return the model class using your created constructor.
            return new MerchCategory(id, name, tax);
        }
    }
    public static DBMapper<MerchCategory> getMap() {
        // return the result mapping method.
        return new MerchCatMap();
    }    
```
##### Table enum:
Table enums allow binding the model class with a valid table name. All parsers require this method.
```java
    public static Table getTable() {
        //The enum you want to assign to the model
        return Table.MerchCategory;
    }
```

#### Final result:
```java
import dbConnect.DBMapper;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.enums.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MerchCategory {
    @AutomaticField @PrimaryField
    private int merch_cat_id;

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
    
    // Allow parsing the class with desired output
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
```

### DBConnect initialization:
To initialize the project, you need to call the initialize method from DBConnect in your main class or where your start-up initialization is.

##### Example initialization:
The initialize method of DBConnect has various overloads to fit your need. In this example we will connect to a local database on default settings, named `store_db`. 
```java
import dbConnect.*;

public class Main {
    public static void main(String[] args){
        DBConnect.initialize("store_db");
    }
}
```
However, if you also want the call to be shorter, you can modify the default string method `defaultConn()` in `dbConnect.ConnectorString` to match your database's information.
```java
    public static ConnectorString defaultConn() {
        return new ConnectorString("localhost", 3306, "store_db", "root", "root");
    }
```
After this, you can simply call `DBConnect.initialize()`. Then you are good to go!

### DBConnect example usage:
Assumed you initialized the DBConnect and created a Data Model called Merchandise.
#### Get data from a table:
```java


public void getMerchandise() {
    // Create a list of merchandises.
    List<Merchandise> merchandiseList = DBConnect.retrieveAll(Merchandise.class);
    if (merchandiseList != null) {
        for (Merchandise merch : merchandiseList) {
            System.out.println(merch);
        }
    }
}

public void getMerchandise(String id) {
    // Set search condition, in this case it is searching for a merchandise matched given ID.
    String condition = "merch_id = ?";
            
    // Even if only expect one object, the method returns a list, so you should make a list here
    List<Merchandise> merchList = DBConnect.retrieve(Merchandise.class, condition, id);
    
    if (!merchList.isEmpty()) {
        Merchandise merch =(Merchandise) merchList.get(0);
        System.out.println(merch);
    }
}
```

#### Insert data into a table:
```java

// Create an instant of the data model you want to add to the database, this constructor is base on what created in the model
private Merchandise newMerch = new Merchandise("Example name", 10.5, 20, 5.0, 1, "f9a78bf4-a30d-11ef-9a3b-d8bbc1b40ca4");

public void insertMerchandise() {
    // insert method itself already handle error, buf if you want custom error handling, wrap the code in a try catch block
    boolean success = DBConnect.insert(newMerch);
    
    if (success) {
        System.out.println("Insert into database successfully");
    } else {
        System.out.println("Insert into database failed");
    }
}
```

#### Update data of a table:
```java
private Merchandise targetMerch = new Merchandise("2e54f046-a8ab-11ef-9a3b-d8bbc1b40ca4", "Update Example name", 120.7, 245.3, 10.5, 2, "f9a78bf4-a30d-11ef-9a3b-d8bbc1b40ca4");

public void updateMerchandise() {
    boolean success = DBConnect.update(targetMerch);

    if (success) {
        System.out.println("Update database successfully");
    } else {
        System.out.println("Update database failed");
    }
}
```

#### Delete data of a table;
```java
public void deleteMerchandise() {
    // Current delete parser only cares about the primary key of the model.
    Merchandise merch = new Merchandise();
    merch.setMerch_id("2e54f046-a8ab-11ef-9a3b-d8bbc1b40ca4");
    
    boolean success = DBConnect.delete(merch);

    if (success) {
        System.out.println("Delete data successfully");
    } else {
        System.out.println("Delete data failed");
    }
}
```
