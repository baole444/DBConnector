package dbConnect.query;

import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import dbConnect.mapper.MongoMapper;
import dbConnect.mapper.SQLMapper;
import org.bson.Document;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MongoDBQuery implements DBInterface {
    private final MongoDatabase mongoDatabase;
    private MongoCollection<Document> collection;
    private int rowCount = 0;
    private boolean initState = false;


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

    /**
     * <div>
     *     Method uses to initiate MongoDB CRUD operation.
     * </div>
     * <div>
     *     Supported chain method:
     *     <ul>
     *     <li>{@link #insert(Document)}</li>
     *     <li>{@link #delete(Document)}</li>
     *     <li>{@link #update(Document, Document)}</li>
     *     </ul>
     * </div>
     * <div>
     *     To return number of affected entries, call {@link #count()} at the end of chain.
     * </div>
     * @param collectionName the collection to perform operation on.
     */
    @Override
    public MongoDBQuery setMongoData(String collectionName) {
        this.collection = mongoDatabase.getCollection(collectionName);
        this.initState = true;
        return this;
    }

    /**
     * MongoDB insert operation.
     * @param document the entry to be inserted
     */
    public MongoDBQuery insert(Document document) {
        checkInit();

        InsertOneResult result = collection.insertOne(document);

        rowCount = result.wasAcknowledged() ? 1 : 0;

        return this;
    }

    /**
     * MongoDB delete operation
     * @param filter condition(s) to match for deletion.
     */
    public MongoDBQuery delete(Document filter) {
        checkInit();

        DeleteResult result = collection.deleteMany(filter);

        rowCount = (int) result.getDeletedCount();

        return this;
    }

    /**
     * MongoDB update operation.
     * @param filter condition(s) to match for updating.
     * @param update values to update the entries with.
     */
    public MongoDBQuery update(Document filter, Document update) {
        checkInit();

        UpdateResult result = collection.updateMany(filter, update);
        rowCount = (int) result.getModifiedCount();

        return this;
    }

    /**
     * Get the number of entries affected by data operation.
     * @return number of affected entries.
     */
    public int count() {
        return rowCount;
    }

    private void checkInit() {
        if (!initState) {
            throw new IllegalStateException("missing setMongoData() in the call chain!");
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
