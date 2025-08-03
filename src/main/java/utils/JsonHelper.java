package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class JsonHelper {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            /*.registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException{
                    out.value(value.toString());
                }
                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    return LocalDate.parse(in.nextString());
                }
            })*/
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