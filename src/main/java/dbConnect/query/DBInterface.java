package dbConnect.query;

import dbConnect.mapper.DataMapper;
import dbConnect.mapper.MongoMapper;
import dbConnect.mapper.ResultSetInterface;
import dbConnect.mapper.SQLMapper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DBInterface {
    /**
     * A low level method to fetch data from an SQL database server.
     * @param query SQL script, often with placeholders.
     * @param model A Mapping method of a Data Model Class. It can be obtained via {@code getMap()}.
     * @param params Values for placeholders in corresponding order.
     * @return {@link List} of instances of a specified data model.
     * @param <T> Object
     * @throws SQLException when there is an error occurred during execution.
     */
    <T> List<T> loadSQLData(String query, SQLMapper<T> model, Object... params) throws SQLException;

    /**
     * A low level method to fetch data from a Mongo database server.
     * @param collectionName the collection to query.
     * @param model a Mapping method of a Data Model Class. It can be obtained via {@code getMap()}.
     * @return {@link List} of instances of a specified data model.
     * @param <T> Object
     */
    <T> List<T> loadMongoData(String collectionName, Document filter, Document projection, MongoMapper<T> model);

    /**
     * A low level method to update data with an SQL database server.
     * @param query SQL script, often with placeholders.
     * @param params Values for placeholders in corresponding order.
     * @throws SQLException when there is an error occurred during execution.
     */
    int setDataSQL(String query, Object... params) throws SQLException;

    void insertMongoData(String collectionName, Document document);
}
