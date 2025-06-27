package dbConnect.mapper;

import com.mongodb.MongoException;
import org.bson.Document;

/**
 * An interface that handles mapping of {@link Document}.
 * @param <T> a {@code DataModel} Object that required parsing.
 */
public interface DocumentInterface<T> {
    /**
     * Map the query result to an instance of a data model.
     * @param document instance of a Document
     * @return instance of a Data Model with its attributes from the query result.
     * @throws MongoException when error occurred during mapping between MongoDB query result and the DataModel.
     */
    T map(Document document) throws MongoException;
}
