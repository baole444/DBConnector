package dbConnect.mapper;

/**
 * A generic mapper interface that accepts different source type.
 * @param <T> Object, pass in of Query's return.
 * @param <S> {@link java.sql.ResultSet} or {@link org.bson.Document}
 */
public interface DataMapper<T, S> {
    T map(S source) throws Exception;
}
