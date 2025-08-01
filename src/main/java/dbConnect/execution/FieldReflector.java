package dbConnect.execution;

import dbConnect.models.constrain.MaxLength;
import dbConnect.models.json.JsonField;
import dbConnect.models.json.JsonUtility;
import dbConnect.models.notnull.NotNullField;
import dbConnect.query.MongoDBQuery;
import dbConnect.query.SqlDBQuery;

import java.lang.reflect.Field;

class FieldReflector {
    private final SqlDBQuery sqlDBQuery;
    private final MongoDBQuery mongoDBQuery;

    FieldReflector(SqlDBQuery sqlDBQuery) {
        this.sqlDBQuery = sqlDBQuery;
        this.mongoDBQuery = null;
    }

    FieldReflector(MongoDBQuery mongoDBQuery) {
        this.sqlDBQuery = null;
        this.mongoDBQuery = mongoDBQuery;
    }

    /**
     * Internal method to get the value of a field.
     * @param model an instance of a Data Model.
     * @param field an attribute extracted from a model.
     * @return value of the field as an {@code object}.
     * @param <T> Object.
     * @throws IllegalAccessException when failed to extract field's details.
     */
    <T> Object getFieldValue(T model, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(model);
        // Verify value for not null field
        if (field.isAnnotationPresent(NotNullField.class) && fieldValue == null) {
            throw new IllegalArgumentException("Value of field '" + field.getName() + "' with not null annotation is null");
        }

        if (fieldValue != null && field.isAnnotationPresent(JsonField.class)) {
            if (sqlDBQuery != null && mongoDBQuery == null) {
                fieldValue = sqlJsonHandler(field, fieldValue);
            }

            if (sqlDBQuery == null && mongoDBQuery != null) {
                fieldValue = mongoJsonHandler(field, fieldValue);
            }
        }

        fieldValue = applyMaxLength(field, fieldValue);

        return fieldValue;
    }

    private Object sqlJsonHandler(Field field, Object val) {
        JsonField jsonField = field.getAnnotation(JsonField.class);
        try {
            return JsonUtility.toJson(val, jsonField.ignoreNulls());
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize JSON field '" + field.getName() + "': "+ e.getMessage(), e.getCause());
        }
    }

    private Object mongoJsonHandler(Field field, Object val) {
        JsonField jsonField = field.getAnnotation(JsonField.class);

        if (jsonField.storeAsString()) {
            try {
                return JsonUtility.toJson(val, jsonField.ignoreNulls());
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to serialize JSON field '" + field.getName() + "': "+ e.getMessage(), e.getCause());
            }
        }

        return val;
    }

    private Object applyMaxLength(Field field, Object fieldValue) {
        if (field.isAnnotationPresent(MaxLength.class)) {
            if (fieldValue instanceof String s) {
                int limit = field.getAnnotation(MaxLength.class).value();

                if (s.length() > limit) return s.substring(0, limit);
            } else {
                throw new IllegalArgumentException("Field '" + field.getName() + "' with max length annotation is not a string");
            }
        }

        return fieldValue;
    }
}
