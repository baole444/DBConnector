package dbConnect.query;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import dbConnect.mapper.MongoMapper;
import dbConnect.mapper.SQLMapper;
import org.bson.Document;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MongoDBQuery implements DBInterface {
    private final MongoDatabase mongoDatabase;

    public MongoDBQuery(String connectionString, String dbName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        this.mongoDatabase = mongoClient.getDatabase(dbName);
    }

    /**
     * A low level method to fetch data from a Mongo database server.
     * @param collectionName the collection to query.
     * @param filter data filtering conditions.
     * @param projection limit on which field to return.
     * @param model a Mapping method of a Data Model Class. It can be obtained via {@code getMap()}.
     * @return {@link List} of instances of a specified data model.
     * @param <T> Type of the data model
     */
    @Override
    public <T> List<T> loadMongoData(String collectionName, Document filter, Document projection, MongoMapper<T> model) {
        List<T> rows = new ArrayList<>();

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        for (Document doc : collection.find(filter).projection(projection)) {
            rows.add(model.map(doc));
        }

        return rows;
    }

    @Override
    public void setMongoData() {
        throw new UnsupportedOperationException("MongoDB does not support parsing string as query, please use specific method for CRUD instead.");
    }

    public int insertMongoData(String collectionName, Document document) {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            collection.insertOne(document);
            return 1;
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert data into collection: " + e.getMessage());
        }
    }

    public int deleteMongoData(String collectionName, Document filter) {
        try {
            MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
            DeleteResult result = collection.deleteMany(filter);
            return (int) result.getDeletedCount();
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert data into collection: " + e.getMessage());
        }
    }

    @Override
    public <T> List<T> loadSQLData(String query, SQLMapper<T> model, Object... params) throws SQLException {
        throw new UnsupportedOperationException("SQL operation not allowed in Mongo queries.");
    }

    @Override
    public int setDataSQL(String query, Object... params) throws SQLException {
        throw new UnsupportedOperationException("SQL operation not allowed in Mongo queries.");
    }
}
