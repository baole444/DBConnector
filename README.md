# General Database Connector
An abstraction for JDBC and MongoDB data query tasks.

## Feature:
- Parsing query statements base on data model.
- Insert, Update, Delete, Retrieve data with ease.
- MongoDB support.

## Installation:
This project uses Gradle (version 8.13) for importation of dependencies and build tasks, 
make sure to add this to your `build.gradle`:
### Example build.gradle:
```Groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.mysql:mysql-connector-j:9.2.0")
    implementation("org.mongodb:mongodb-driver-sync:5.3.1")
    implementation("org.mongodb:bson:5.3.1")
    implementation("com.google.code.gson:gson:2.12.1")
}
```
For additional setup, please see the project's [build.gradle](https://github.com/baole444/DBConnector/blob/main/build.gradle) file

<details>
    <summary>Build and compile it for yourself</summary>

Since version 2.0, the project will drop java module as it is unnecessary.<br>
The project build script uses gradle's standard build and jar command.<br>

If wrapper is missing:
```console
gradle wrapper
```
To clean build, run:
```console
./gradlew clean build jar
```
Or if you don't need to clean the build directory:
```console
./gradlew build jar
```

The task will automatically generate three jars under `build\libs` which are:
- `DBConnector-version-source.jar`
- `DBConnector-version-javadoc.jar`
- `DBConnector-version.jar` (standalone)

</details>

> [!IMPORTANT]   
> Since `version 2.1`, Table/Collection Enum will be deprecated in favour of Annotation.<br>
> If you previously used these Enums to register table/collection's name, make sure to migrate to new annotation system.<br>
> Support and check for these Enum might be removed in future update.

### Create a data model Class:
The current abstraction allows user to create their own data model following the supported structure
with each attribute represent a column and its name.<br>
This abstraction had provided a `DataModel<T>` interface for required method within a data model.

#### Annotation for table/collection name:
Since `version 2.1`, you will be able to annotate the name of the table/collection that your data model
based on at the Class level annotation:
- `@TableName("your_table_name")` for table name.
- `@CollectionName("your_collection_name")` for collection name.

Examples:
```java
import dbConnect.models.meta.TableName;
import dbConnect.models.meta.CollectionName;

@TableName("Example")
@CollectionName("Example")
public class Example extends DataModel<Example> {
    // Existing attributes and methods.
}
```

#### Annotation for an attribute:
Currently, the system supports these following annotation:
- `@AutomaticField` for field managed by the database.
- `@PrimaryField` for (SQL) primary key field.
- `@MaxLength` for limit a field's string length. The default value for it is 255.
- `@MongoOnly` for limit field access to MongoDB only.
- `@MySQLOnly` for limit field access to MySQL only
- `@NotNullField` for field that can’t be null.

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

@NotNullField @MaxLength(36) @MySQLOnly
private String foreign_key;
```

> [!NOTE]   
> Annotations are not required, but it will give a hint on parser to manage your query better.<br>
> Table/Collection name annotation are required, if not, the system will fall back to the deprecated Enum.
> `@AutomaticField` will always take priority over `@NotNullField`.

#### Create an example model:
Your data model should contain some method for initialization such as:
- Annotation for Table or Collection name depends on your model usage.
- An empty constructor for getting instance of class. (Required by parsing methods)
```java
import dbConnect.DataModel;
import dbConnect.models.meta.TableName;
import dbConnect.models.meta.CollectionName;

@TableName("Example")
@CollectionName("Example")
public class Example extends DataModel<Example> {
    public Example() {}
    
    // Other constructor-overloads and methods.
}
```
- Necessary getter and setter methods for your convenience.
- Result mapping to generate new instance of data model during data retrieval.
- if you extend the built in `DataModel<T>` for your class it will ensure retrieval  method and return mapper methods.

> [!NOTE]
> Some MongoDB method's fall back logic rely on `@MongoOnly` field with name "_id" to perform ObjectId base execution.
> Missing this field could cause some unexpected behaviour.
> This will be changed in a later version of the package.

Here is the implement of the [Example data model](https://github.com/baole444/DBConnector/blob/main/Example%20Models/Example.java)
that supports both mySQL and mongoDB:
<details>
    <summary><b>Example.class</b></summary>

```java
package your_package;

import com.mongodb.MongoException;
import dbConnect.DataModel;
import dbConnect.mapper.DocumentInterface;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.models.autogen.AutomaticField;
import dbConnect.models.autogen.PrimaryField;
import dbConnect.models.constrain.MaxLength;
import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.constrain.MySQLOnly;
import dbConnect.models.notnull.NotNullField;
import dbConnect.models.meta.TableName;
import dbConnect.models.meta.CollectionName;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.ResultSet;
import java.sql.SQLException;

@TableName("Example")
@CollectionName("Example")
public class Example extends DataModel<Example> {
    @AutomaticField @PrimaryField @MaxLength(36) @MySQLOnly
    private String uuid;

    @AutomaticField @MongoOnly
    private ObjectId _id; // Crucial for MongoDB method fallback.

    @NotNullField @MaxLength(100)
    private String user_name;

    private float balance;

    public Example() {}// Must have

    public Example(String uuid, String user_name, float balance) {
        this.uuid = uuid;
        this.user_name = user_name;
        this.balance = balance;
    }

    public Example(ObjectId _id, String user_name, float balance) {
        this._id = _id;
        this.user_name = user_name;
        this.balance = balance;
    }

    public Example(String user_name, float balance) {
        this.user_name = user_name;
        this.balance = balance;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
    
    public static class ExampleSQLMapper implements ResultSetInterface<Example> {
        @Override
        public Example map(ResultSet resultSet) throws SQLException {
            String id = resultSet.getString("uuid");
            String userName = resultSet.getString("user_name");
            float balance = resultSet.getFloat("balance");
            return new Example(id, userName, balance);
        }
    }

    public static class ExampleMongoMapper implements DocumentInterface<Example> {
        @Override
        public Example map(Document document) throws MongoException {
            ObjectId id = document.getObjectId("_id");
            String userName = document.getString("user_name");
            float balance = document.getDouble("balance").floatValue();
            return new Example(id, userName, balance);
        }
    }
    
    @Override
    public ResultSetInterface<Example> getTableMap() {
        return new ExampleSQLMapper();
    }
    
    @Override
    public DocumentInterface<Example> getCollectionMap() {
        return new ExampleMongoMapper();
    }
}
```
</details>

### DBConnect initialization:
To initialize the project, you need to call the initialize method from DBConnect in your main class or where your start-up initialization is.

It can also be called during runtime that will reset the connection.

Switching between MongoDB and MySQL connection can simply be done by calling either `DBConnect.initializeSQL(args)` or `DBConnect.initializeMongo(args)`.

##### Example initialization:
The initialize method of DBConnect has various overloads to fit your need. In this example we will connect to a local database on default settings, named `store_db`. 
```java
import dbConnect.*;

public class Main {
    public static void main(String[] args){
        DBConnect.initializeSQL("store_db");
        // DBConnect.initializeMongo("store_db");
    }
}
```

### DBConnect example usage:
Assumed you initialized the DBConnect and created a Data Model called Example.

<details>
    <summary>Get data from a table</summary>

```java
public void getExample() {
    List<Example> exampleList = DBConnect.retrieveAll(Example.class);
    if (exampleList != null) {
        for (Example example : exampleList) {
            System.out.println(example);
        }
    }
}

public void getExampleSQL(String id) {
    // In this case, we are searching base on uuid
    String condition = "uuid = ?";

    // Even if only expect one object, the method returns a list, so you should make a list here
    List<Example> exampleList = DBCconnect.retrieve(Example.class, condition, id);
    
    if (!exampleList.isEmpty()) {
        Example example = exampleList.getFirst();
        System.out.println(example);
    }
}

public void getExampleMongo(ObjectId id) {
    // In this case, we are searching base on _id
    // Make sure to try-catch for parsing id error.
    // Starting and ending "{}" bracket can be ommited.
    String condition = "_id : ?";

    // Even if only expect one object, the method returns a list, so you should make a list here
    List<Example> exampleList = DBCconnect.retrieve(Example.class, condition, id);

    if (!exampleList.isEmpty()) {
        Example example = exampleList.getFirst();
        System.out.println(example);
    }
}
```
</details>

<details>
    <summary>Insert data into a model</summary>

```java
// Insert a customer name "Ben" with a balance of 100.5
public void insertExample(String name, float balance) {
    Example newExample = new Example(name, balance);
    
    boolean success = DBConnect.insert(newExample);
    
    if (success) {
        System.out.println("Insert into database successfully");
    } else {
        System.out.println("Insert into database failed");
    }
}

insertExample("Ben", 100.5);
```
</details>

<details>
    <summary>Update data of a model</summary>

```java

public void updateExample(String name, Example value) {
    // for this example, we update by name.
    String condition;
    if (usingMongoDB) {
        condition = "user_name : ?";
    } else {
        condition = "user_name = ?";
    }
    
    boolean success = DBConnect.update(value, condition, name);

    if (success) {
        System.out.println("Update database successfully");
    } else {
        System.out.println("Update database failed");
    }
}

// We update entries where the name is "Ben" with new value of "Dover" and balance field of 200.5
updateExample("Ben", new Example("Dover",  200.5));
```

If you call `DBConnect.update(instance)`,
the parser will default to `PrimaryField` or `MongoOnly` field of that instance of data model.
If this is what you wanted,
make sure to initiate the instance with at least primary key field not null or mongo only field not null.
</details>

<details>
    <summary>Delete data of a model</summary>

```java
public void deleteExample(String name, Example instance) {
    String condition;
    if (usingMongoDB) {
        condition = "user_name : ?";
    } else {
        condition = "user_name = ?";
    }
    
    boolean success = DBConnect.delete(instance, condition, name);

    if (success) {
        System.out.println("Update database successfully");
    } else {
        System.out.println("Update database failed");
    }
}
// We delete all entries where the name is "Dover"
deleteExample("Dover", new Example());
```

If you call `DBConnect.delete(instance)`,
the parser will default to `PrimaryField` or `MongoOnly` field of that instance of data model.
If this is what you wanted,
make sure to initiate the instance with at least primary key field not null or mongo only field not null.
</details>