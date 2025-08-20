package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class JsonHelper {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    //Serialize Object > JSON
    public static String toJson(Object object){
        Objects.requireNonNull(object, "Input object cannot be null");
        return gson.toJson(object);
    }

    //Deserialize Json > Object
    public static <T> T fromJson(String json, Class<T> type){
        try{
            return gson.fromJson(Objects.requireNonNull(json, "JSON string cannot be null."), type);
        } catch (JsonSyntaxException e){
            throw new JsonParseException("Malformed JSON: " + json, e);
        }
    }

    //Deserialize Json > List of Objects
    public static <T> List<T> fromJsonList(String json, Class<T> elementType){
        try{
            Type type = TypeToken.getParameterized(List.class, elementType).getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e){
            throw new JsonParseException("Failed to parse JSON list", e);
        }
    }
}