package dbConnect.mapper;

import java.util.concurrent.ConcurrentHashMap;

public class AutomaticMapper {
    private static final ConcurrentHashMap<Class<?>, ResultSetInterface<?>> sqlMappers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, DocumentInterface<?>> mongoMappers = new ConcurrentHashMap<>();

    private AutomaticMapper() {}

    @SuppressWarnings("unchecked")
    public static <T> ResultSetInterface<T> getSQLMapper(Class<T> modelClass) {
        return (ResultSetInterface<T>) sqlMappers.computeIfAbsent(modelClass, c -> new SQLMapper<>((Class<T>) c));
    }

    @SuppressWarnings("unchecked")
    public static <T> DocumentInterface<T> getMongoMapper(Class<T> modelClass) {
        return (DocumentInterface<T>) mongoMappers.computeIfAbsent(modelClass, c -> new MongoMapper<>((Class<T>) c));
    }

    public static void clearCache() {
        sqlMappers.clear();
        mongoMappers.clear();
    }

    public static void clearCache(Class<?> modelClass) {
        sqlMappers.remove(modelClass);
        mongoMappers.remove(modelClass);
    }
}
