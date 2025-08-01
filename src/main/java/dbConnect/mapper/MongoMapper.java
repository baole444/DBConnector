package dbConnect.mapper;

import com.mongodb.MongoException;
import dbConnect.models.constrain.MySQLOnly;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MongoMapper<T> implements DocumentInterface<T> {
    private final Class<T> modelClass;
    private final Map<String, Field> fields;
    private final Constructor<T> constructor;

    public MongoMapper(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.fields = new HashMap<>();

        try {
            this.constructor = modelClass.getDeclaredConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Model class '" + modelClass.getName() + "' must have an empty constructor: ", e);
        }

        initFields();
    }

    @Override
    public T map(Document document) throws MongoException {
        try {
            T instance = constructor.newInstance();

            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                String fieldName = entry.getKey();
                Field field = entry.getValue();

                try {
                    if (document.containsKey(fieldName)) {
                        Object val = getValue(document, field, fieldName);
                        if (val != null) field.set(instance, val);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to map field " + fieldName + "' in '" + modelClass.getName() + "': " + e.getMessage());
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of '" + modelClass.getName(), e);
        }
    }

    public Class<T> getModelClass() {
        return modelClass;
    }

    private void initFields() {
        Field[] fields = modelClass.getDeclaredFields();
        int mods;

        for (Field f : fields) {
            mods = f.getModifiers();

            if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) continue;

            if (f.isAnnotationPresent(MySQLOnly.class)) continue;

            f.setAccessible(true);
            this.fields.put(f.getName(), f);
        }
    }

    private Object getValue(Document document, Field field, String fieldName) {
        Class<?> type = field.getType();
        Object val = document.get(fieldName);

        if (val == null) return null;

        if (type == String.class) return val.toString();

        if (type == ObjectId.class) {
            if (val instanceof ObjectId) return val;
            if (val instanceof String s) return new ObjectId(s);
            return null;
        }

        if (type == int.class || type == Integer.class) {
            if (val instanceof Number n) return n.intValue();
            return null;
        }

        if (type == long.class || type == Long.class) {
            if (val instanceof Number n) return n.longValue();
            return null;
        }

        if (type == float.class || type == Float.class) {
            if (val instanceof Number n) return n.floatValue();
            return null;
        }

        if (type == double.class || type == Double.class) {
            if (val instanceof Number n) return n.doubleValue();
            return null;
        }

        if (type == boolean.class || type == Boolean.class) {
            return switch (val) {
                case Boolean b -> val;
                case Number n -> n.intValue() != 0;
                case String s -> Boolean.parseBoolean(s);
                default -> null;
            };
        }

        if (type == byte.class || type == Byte.class) {
            if (val instanceof Number n) n.byteValue();
            return null;
        }

        if (type == short.class || type == Short.class) {
            if (val instanceof Number n) n.shortValue();
            return null;
        }

        if (type == Date.class) {
            if (val instanceof Date) return val;
            if (val instanceof Number n) return new Date(n.longValue());
            return null;
        }

        if (type == LocalDate.class) {
            if (val instanceof Date d) return Instant
                    .ofEpochMilli(d.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (val instanceof Number n) return Instant
                    .ofEpochMilli(n.longValue())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            return null;
        }

        if (type == LocalDateTime.class) {
            if (val instanceof Date d) return Instant
                    .ofEpochMilli(d.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (val instanceof Number n) return Instant
                    .ofEpochMilli(n.longValue())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            return null;
        }

        if (type == byte[].class) {
            if (val instanceof byte[]) return val;
            return null;
        }

        if (type.isEnum()) {
            if (val instanceof String s) {
                try {
                    return Enum.valueOf((Class<Enum>) type, s);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid enum value '" + val + "' for '" + fieldName + "'");
                    return null;
                }
            }

            return null;
        }

        return val;
    }
}
