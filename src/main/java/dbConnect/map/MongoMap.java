package dbConnect.map;

import dbConnect.mapper.DocumentInterface;
import org.bson.Document;

public class MongoMap<T> implements DataMap<T, Document> {
    private final DocumentInterface<T> mapper;

    public MongoMap(DocumentInterface<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(Document document) {
        return mapper.map(document);
    }
}
