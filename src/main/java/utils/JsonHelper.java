package utils;

import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

public class JsonHelper {

    private static final ObjectMapper mapper = new ObjectMapper() {

        @Override
        public Object deserialize(ObjectMapperDeserializationContext objectMapperDeserializationContext) {
            return null;
        }

        @Override
        public Object serialize(ObjectMapperSerializationContext objectMapperSerializationContext) {
            return null;
        }
    };


}