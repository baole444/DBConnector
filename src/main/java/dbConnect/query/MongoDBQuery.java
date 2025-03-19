package dbConnect.query;

import com.mongodb.client.MongoDatabase;
import dbConnect.mapper.MongoMapper;
import dbConnect.mapper.SQLMapper;
import org.bson.Document;

import java.sql.SQLException;
import java.util.List;

public class MongoDBQuery implements DBInterface {
    private MongoDatabase mongoDatabase;

    /**
     * A low level method to fetch data from a Mongo database server.
     *
     * @param collectionName the collection to query.
     * @param model          a Mapping method of a Data Model Class. It can be obtained via {@code getMap()}.
     * @return {@link List} of instances of a specified data model.
     */
    @Override
    public <T> List<T> loadMongoData(String collectionName, MongoMapper<T> model) {
        return List.of();
    }


    @Override
    public void insertMongoData(String collectionName, Document document) {

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
