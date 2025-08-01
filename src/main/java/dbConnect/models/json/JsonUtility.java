package dbConnect.models.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

public class JsonUtility {
    private static final Gson gsonNullable = new GsonBuilder().serializeNulls().create();
    private static final Gson gsonNotNull = new GsonBuilder().create();

    public static String toJson(Object val) {
        return toJson(val, false);
    }

    public static String toJson(Object val, boolean ignoreNulls) {
        if (val == null) return null;

        try {
            Gson use = ignoreNulls ? gsonNotNull : gsonNullable;
            return use.toJson(val);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON: " + e.getMessage(), e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String jsonString, Type type) {
        if (jsonString == null || jsonString.trim().isEmpty()) return null;

        try {
            return (T) gsonNullable.fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Failed to deserialize JSON string: " + e.getMessage(), e.getCause());
        }
    }
}
