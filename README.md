> [!WARNING]
> Project under heavy refactoring with NoSQL MongoDB support.
> Implementation might introduce breaking change.
> Since version 1.7

# General Database Connector
An abstraction for JDBC and MongoDB data query tasks.

## Feature:
- Parsing query statements base on data model.
- Insert, Update, Delete, Retrieve data with ease.
- MongoDB support.

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
    implementation("org.mongodb:mongodb-driver-sync:5.3.1")
    implementation("org.mongodb:bson:5.3.1")
    implementation("com.google.code.gson:gson:2.12.1")
}
```
For additional setup, please see the project's [build.gradle](https://github.com/baole444/DBConnector/blob/main/build.gradle) file

> [!IMPORTANT]   
> Enums used by the system (such as table/collection name) is statically complied at runtime and cannot be added. 
> If you decided to compile the project and add it as a jar file, make sure to update necessary enums or built in data models that you decided.

Create a folder call `Lib` within your project and put the `DBConnect-<version>.jar` inside (You can skip this if you use the source code directly).<br>
Make sure to also put the `mysql-connector-j-<version>.jar` inside if you don't have it as your project dependencies yet.

### Create a data model Class:
The current abstraction allows user to create their own data model following the supported structure
with each attribute represent a column and its name.<br>
This abstraction had provided a `DataModel<T>` interface for required method within a data model.


#### Annotation for an attribute:
Currently, the system supports these following annotation:
- `@AutomaticField` for field managed by the database.
- `@PrimaryField` for (SQL) primary key field.
- `@MaxLength` for limit a field's string length. The default value for it is 255.
- `MongoOnly` for limit field access to MongoDB only.
- `@NotNullField` for field that can’t be nullable.

Examples:
```java
import dbConnect.models.*;
import org.bson.types.ObjectId;

@AutomaticField @PrimaryField @MaxLength(36)
private String id;

@MongoOnly @AutomaticField
private ObjectId _id;

@MaxLength // Not specified length will default to 255.
private String text;

@NotNullField @MaxLength(36)
private String foreign_key;
```

> [!NOTE]   
> Annotations are not required, but it will give a hint on parser to manage your query better.
> `@AutomaticField` will always take priority over `@NotNullField`.

#### Table Enums class:
Current system store table's name in enums, make sure to update `dbConnect.models.enums.Table` to included your data model. At this point, you can also update `DataModel` enum if you decided to use it.

```java
package dbConnect.models.enums;

public enum Table {
    Supplier("supplier"),
    MerchCategory("merch_category"),
    Merch("merchandise"),
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
Result mapping allows streamline call when retrieving data. This method implements `ResultSetInterface` interface to parse the result from a query into your model class instance and return it.
```java
    public static class MerchCatMap implements ResultSetInterface<MerchCategory> {
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
    public static ResultSetInterface<MerchCategory> getMap() {
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
import dbConnect.mapper.ResultSetInterface;
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

    public static class MerchCatMap implements ResultSetInterface<MerchCategory> {
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

    public static ResultSetInterface<MerchCategory> getMap() {
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
After this, you can simply call `DBConnect.initialize()`. Then you’re good to go!

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
