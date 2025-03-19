package dbConnect.mapper;

import org.bson.Document;

public class MongoMapper<T> implements DataMapper<T, Document> {
    private final DocumentInterface<T> mapper;

    public MongoMapper(DocumentInterface<T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public T map(Document document) {
        return mapper.map(document);
    }
}
