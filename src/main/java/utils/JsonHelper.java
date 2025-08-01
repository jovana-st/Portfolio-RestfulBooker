package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JsonHelper {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    //Serialize Object > JSON
    public static String toJson(Object object){
        return gson.toJson(object);
    }

    //Deserialize Json > Object
    public static <T> T fromJson(String json, Class<T> deser){
        return gson.fromJson(json, deser);
    }

    //Deserialize Json > List of Objects
    public static <T> List<T> fromJsonList(String json, Class<T> deser){
        return gson.fromJson(json, TypeToken.getParameterized(List.class, deser).getType());
    }

}