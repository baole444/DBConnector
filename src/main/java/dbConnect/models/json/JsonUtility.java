package dbConnect.models.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonUtility {
    private static final Gson gson = new GsonBuilder().create();

    public static Object toJson(Object val) {
        return gson.toJson(val);
    }

    public static Object fromJson(String jsonString, Type type) {
        return gson.fromJson(jsonString, type);
    }
}
