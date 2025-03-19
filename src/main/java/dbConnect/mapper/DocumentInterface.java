package dbConnect.mapper;

import org.bson.Document;

/**
 * An interface that handles mapping of {@link Document}.
 * @param <T> a {@code DataModel} Object that required parsing.
 */
public interface DocumentInterface<T> {
    T map(Document document);
}
