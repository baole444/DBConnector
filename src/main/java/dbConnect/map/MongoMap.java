package dbConnect.map;

import dbConnect.mapper.DocumentInterface;
import org.bson.Document;

/**
 * An implementation of {@link DataMap} for MongoDB mapping using {@link Document}
 * @param <T> a {@code DataModel} Object that required parsing.
 * @see DocumentInterface
 */
public class MongoMap<T> implements DataMap<T, Document> {
    private final DocumentInterface<T> mapper;

    /**
     * MongoMap default constructor.
     * @param mapper an instance of DocumentInterface
     */
    public MongoMap(DocumentInterface<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(Document document) {
        return mapper.map(document);
    }
}
