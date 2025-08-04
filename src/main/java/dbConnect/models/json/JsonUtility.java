package dbConnect.models.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Handle Json serialization and deserialization.
 */
public class JsonUtility {
    private static final Gson gsonNullable = new GsonBuilder().serializeNulls().create();
    private static final Gson gsonNotNull = new GsonBuilder().create();

    /**
     * Serialize an object to Json string.
     * This method does not ignore nulls.
     *
     * @param val the value that needed to be serialized.
     * @return Json string of the value if serialization success.
     */
    public static String toJson(Object val) {
        return toJson(val, false);
    }

    /**
     * Serialize an object to Json string.
     *
     * @param val the value that needed to be serialized.
     * @param ignoreNulls true to ignore keys with null values.
     * @return Json string of the value if serialization success.
     */
    public static String toJson(Object val, boolean ignoreNulls) {
        if (val == null) return null;

        try {
            Gson use = ignoreNulls ? gsonNotNull : gsonNullable;
            return use.toJson(val);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * Deserialize a Json string back to an object of a certain type.
     *
     * @param jsonString the value to be deserialized.
     * @param type the type of the object to deserialize the string back to.
     * @return an instance of the value as the designated type if deserialization success.
     * @param <T> type of the value.
     */
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
