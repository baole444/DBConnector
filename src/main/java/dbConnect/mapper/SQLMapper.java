package dbConnect.mapper;

import dbConnect.models.constrain.MongoOnly;
import dbConnect.models.json.JsonField;
import dbConnect.models.json.JsonUtility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class SQLMapper<T> implements ResultSetInterface<T> {
    private final Class<T> modelClass;
    private final Constructor<T> constructor;
    private final Map<String, Field> fields;

    public SQLMapper(Class<T> modelClass) {
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
    public T map(ResultSet resultSet) throws SQLException {
        try {
            T instance = constructor.newInstance();

            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                String fieldName = entry.getKey();
                Field field = entry.getValue();

                try {
                    if (hasColumn(resultSet, fieldName)) {
                        Object val = getValue(resultSet, field, fieldName);
                        if (val != null) field.set(instance, val);
                    }
                } catch (SQLException ignored) {
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

            if (f.isAnnotationPresent(MongoOnly.class)) continue;

            f.setAccessible(true);
            this.fields.put(f.getName(), f);
        }
    }

    private boolean hasColumn(ResultSet resultSet, String fieldName) {
        try {
            resultSet.findColumn(fieldName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private Object getValue(ResultSet resultSet, Field field, String fieldName) throws SQLException {
        Class<?> type = field.getType();

        if (field.isAnnotationPresent(JsonField.class)) {
            String jsonString = resultSet.getString(fieldName);
            if (jsonString != null && !jsonString.isEmpty()) {
                return JsonUtility.fromJson(jsonString, type);
            }
        }

        Object val = resultSet.getObject(fieldName);

        if (val == null) return null;

        if (type == String.class) return resultSet.getString(fieldName);

        if (type == int.class || type == Integer.class) return resultSet.getInt(fieldName);

        if (type == long.class || type == Long.class) return resultSet.getLong(fieldName);

        if (type == float.class || type == Float.class) return resultSet.getFloat(fieldName);

        if (type == double.class || type == Double.class) return resultSet.getDouble(fieldName);

        if (type == boolean.class || type == Boolean.class) return resultSet.getBoolean(fieldName);

        if (type == byte.class || type == Byte.class) return resultSet.getByte(fieldName);

        if (type == short.class || type == Short.class) return resultSet.getShort(fieldName);

        if (type == java.sql.Date.class) return resultSet.getDate(fieldName);

        if (type == java.sql.Time.class) return resultSet.getTime(fieldName);

        if (type == java.sql.Timestamp.class) return resultSet.getTimestamp(fieldName);

        if (type == java.util.Date.class) {
            java.sql.Timestamp timestamp = resultSet.getTimestamp(fieldName);
            return timestamp != null ? new java.util.Date(timestamp.getTime()) : null;
        }

        if (type == LocalDate.class) {
            java.sql.Date date = resultSet.getDate(fieldName);
            return date != null ? date.toLocalDate() : null;
        }

        if (type == LocalDateTime.class) {
            java.sql.Timestamp timestamp = resultSet.getTimestamp(fieldName);
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        }

        if (type == byte[].class) return resultSet.getBytes(fieldName);

        if (type.isEnum()) {
            String enumVal = resultSet.getString(fieldName);
            if (enumVal != null) return Enum.valueOf((Class<Enum>) type, enumVal);

            return null;
        }

        return resultSet.getObject(fieldName);
    }
}
