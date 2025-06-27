package dbConnect.map;

/**
 * A generic mapper interface that accepts different source type.
 * @param <T> Object, pass in of Query's return.
 * @param <S> {@link java.sql.ResultSet} or {@link org.bson.Document}
 */
public interface DataMap<T, S> {
    /**
     * Mapping interface.
     * @param source source to map from.
     * @return instance of an Object conformed to the destination type.
     * @throws Exception when error occurred during mapping
     */
    T map(S source) throws Exception;
}
