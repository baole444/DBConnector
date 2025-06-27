package dbConnect.models;

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
import dbConnect.models.meta.*;
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
    private ObjectId _id;

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
