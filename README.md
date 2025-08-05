# General Database Connector
An abstraction for JDBC and MongoDB data query tasks.

<i>This document is for `v2.5`</i>

## Feature:
- Parsing query statements base on data model.
- Insert, Update, Delete, Retrieve data with ease.
- Relationship query supported.
- MongoDB and MySQL supported.

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
- `@PrimaryField` for primary field or unique identifier in the database.
- `@MaxLength` for limit a field's string length. The default value for it is 255.
- `@MongoOnly` for limit field access to MongoDB only.
- `@MySQLOnly` for limit field access to MySQL only
- `@NotNullField` for field that cannot be null.
- `@JsonField` for field of a serializable type that is stored as JSON string or nested document in database.

Additionally, the system also supports relationship annotation:
- `@ForeignKey` for field that hold the instance(s) of the related data model where the foreign key point to.
- `@OneToOne` for field of a `Target` type that hold one-to-one relationship.
- `@OneToMany` for field of a `Collection<Target>` type that hold one-to-many relationship.
- `@ManyToOne` for field of a `Target` that hold many-to-many relationship.

Examples:
```java
@AutomaticField @PrimaryField(forMongo = false) @MaxLength(36)
private String id;

@AutomaticField @MongoOnly @PrimaryField(forSQL = false)
private ObjectId _id;

// this unique identifier is the same on both database
@PrimaryField
private String uniqueId;

@MaxLength // Not specified length will default to 255.
private String text;

@NotNullField @MySQLOnly
private String fullName;

@JsonField(ignoreNulls = true)
private List<CustomModel> model;
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
- Optionally, Result mapping override to generate new instance of data model during data retrieval.
- if you extend the built in `DataModel<T>` for your class it will ensure retrieval method and return mapper methods.

> [!NOTE]
> Since `version 2.5`, `DataModel` will automatically map your custom model using `AutomaticMapper`.<br>
> Unless you need to customize your mapping (custom name or special ignore case), there is no need to override
> the automatic mapper.

Here is the implement of the [Example data models](https://github.com/baole444/DBConnector/blob/main/Example%20Models/)
that supports both mySQL and mongoDB:
<details>
    <summary><b>Example.class</b></summary>

```java
@TableName("Example")
@CollectionName("Example")
public class Example extends DataModel<Example> {
    @AutomaticField @PrimaryField @MaxLength(36) @MySQLOnly
    private String uuid;

    @NotNullField @MaxLength(100)
    private String user_name;

    private float balance;

    @JsonField
    private HashMap<String, Object> examplesDetails;
    
    @ForeignKey(column = "detail_id", referencedColumnName = "id", nullable = false)
    private ExampleDetail detail;
    
    @OneToMany(targetModel = OtherExampleModel.class, mappedBy = "other_example_id", fetch = FetchMethod.EAGER, cascade = {CascadeType.PERSIST ,CascadeType.REMOVE})
    private List<OtherExampleModel> otherExamples;
    
    public Example() {}// Must have
    
    // Other methods you might want to add.
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

Parsing with relationship:

For example, we have Customer, Order and CustomerProfile models with relationship keys as follows:
```java
public class Customer extends DataModel<Customer> {
    @OneToOne(targetModel = CustomerProfile.class, fetch = FetchMethod.EAGER)
    @ForeignKey(column = "profile_id")
    private CustomerProfile profile;
    
    @OneToMany(targetModel = Order.class, mappedBy = "customer_id", fetch = FetchMethod.LAZY)
    private List<Order> orders;
}
```
```java
public class Order extends DataModel<Order> {
    @ManyToOne(targetModel = Customer.class, fetch = FetchMethod.LAZY)
    @ForeignKey(column = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;
}
```

<details>
    <summary>Retrieve with relationship</summary>

```java
public void getOrders() {
    // Get customers with relationships
    List<Customer> customers = DBConnect.retrieveAllRelationships(Customer.class);

    // Get a specific customer with relationships
    String condition;
    if (usingMongoDB) {
        condition = "customer_name : ?";
    } else {
        condition = "customer_name = ?";
    }
    
    List<Customer> customer = DBConnect.retrieveRelationships(Customer.class, condition, "John Doe");

    // Access the loaded relationships
    if (!customer.isEmpty()) {
        Customer johnDoe = customer.get(0);
        CustomerProfile profile = johnDoe.getProfile();

        // load relationship passively
        DBConnect.loadLazyRelationships(johnDoe);
        List<Order> orders = johnDoe.getOrders();
    } 
}
```
</details>

<details>
    <summary>Retrieve related data</summary>

```java
public void getRelated() {
    // Get orders belong to John Doe
    Customer customer = retrieveCustomer("John Doe");
    List<Order> customerOrders = DBConnect.retrieveRelated(customer, "orders");
    
    // Get customers belong to an order
    Order order = retrieveOrder("some bill");
    List<Customer> customers = DBConnect.retrieveRelated(order, "customer");
}
```
</details>

<details>
    <summary>Insert with relationship</summary>

```java
public void insertInformation() {
    // Customer John Doe
    Customer john = new Customer("John Doe", "john@example.com", "1234567890");

    // Work as a developer
    CustomerProfile profile = new CustomerProfile("Developer", new Date(), "M");

    john.setProfile(profile);

    List<Order> orders = new ArrayList<>();
    
    // Order 1 and 2 where customer_id, date and total is set
    // As we are cascading this, we can skip the id
    Order order1 = new Order(new Date(), 20.5);
    Order order2 = new Order(new Date(), 100.0);
    orders.add(order1);
    orders.add(order2);
    
    john.setOrders(orders);
    
    boolean success = DBConnect.insertRelationships(john);
}
```
</details>