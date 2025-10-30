package dbConnect.mapper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle mapper creation and caching of created mappers.
 */
public class AutomaticMapper {
    private static final ConcurrentHashMap<Class<?>, ResultSetInterface<?>> sqlMappers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, DocumentInterface<?>> mongoMappers = new ConcurrentHashMap<>();

    private AutomaticMapper() {}

    /**
     * Get the mapping of the Data Model for MySQL.
     * @param modelClass the target data model class.
     * @return the ResultSet mapper of the data model.
     * @param <T> type of the data mode.
     */
    @SuppressWarnings("unchecked")
    public static <T> ResultSetInterface<T> getSQLMapper(Class<T> modelClass) {
        return (ResultSetInterface<T>) sqlMappers.computeIfAbsent(modelClass, c -> new SQLMapper<>((Class<T>) c));
    }

    /**
     * Get the mapping of the Data Model for MongoDB.
     * @param modelClass the target data model class.
     * @return the Document mapper of the data model.
     * @param <T> type of the data mode.
     */
    @SuppressWarnings("unchecked")
    public static <T> DocumentInterface<T> getMongoMapper(Class<T> modelClass) {
        return (DocumentInterface<T>) mongoMappers.computeIfAbsent(modelClass, c -> new MongoMapper<>((Class<T>) c));
    }

    /**
     * Clear all cached automatic mappers.
     */
    public static void clearCache() {
        sqlMappers.clear();
        mongoMappers.clear();
    }

    /**
     * Clear all automatic mappers of a Data Model.
     * @param modelClass the target data model class.
     */
    public static void clearCache(Class<?> modelClass) {
        sqlMappers.remove(modelClass);
        mongoMappers.remove(modelClass);
    }
}
