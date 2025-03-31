package dbConnect.map;

/**
 * A generic mapper interface that accepts different source type.
 * @param <T> Object, pass in of Query's return.
 * @param <S> {@link java.sql.ResultSet} or {@link org.bson.Document}
 */
public interface DataMap<T, S> {
    T map(S source) throws Exception;
}
