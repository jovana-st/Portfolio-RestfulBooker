package utils;

import com.google.gson.JsonParseException;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class ApiAssertions {


    //Hard assertions
    //Assert that the status code is a specific value
    public static void assertStatusCode(Response response, int expectedStatusCode){
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code: " + expectedStatusCode + " but got: " + response.getStatusCode());
    }

    //Assert that the response contains a specific field
    public static void assertResponseContainsField(Response response, String fieldName){
        Assert.assertNotNull(response.jsonPath().get(fieldName),
                "Response does not contain the field: " + fieldName);
    }

    //Assert that a field in the response equals a specific value
    public static <T> void assertResponseFieldEquals(Response response, String fieldName, T expectedValue){
        Assert.assertEquals(response.jsonPath().get(fieldName), expectedValue,
                "Field: " + fieldName + "mismatch.");
    }

    //Assert that the response time is less than a specific amount
    public static void assertResponseTimeLessThan(Response response, long maxTime){
        Assert.assertTrue(response.getTime() < maxTime,
                "Response time is longer than: " + maxTime);
    }

    //Assert that the response body is empty
    public static void assertEmptyBody(Response response){
        Assert.assertTrue(response.getBody().asString().isEmpty(),
                "Response body is not empty.");
    }

    //Assert that the response body is an empty array
    public static void assertBodyEmptyArray(Response response){
        List<?> items = response.jsonPath().getList("$");
        Assert.assertTrue(items.isEmpty(),
                "Expected empty array but got: " + items.size() + " items");
    }

    //Assert that the header exists
    public static void assertHeaderExists(Response response, String headerName){
        Assert.assertTrue(response.getHeaders().hasHeaderWithName(headerName),
                "Header: " + headerName + "does not exist.");
    }

    //Assert JSON Schema
    public static void assertJsonSchema(Response response, String schemaPath){
        response.then().assertThat().body(matchesJsonSchema(
                new File(ApiAssertions.class.getClassLoader().getResource(schemaPath).getFile())));
    }

    //Asserting boolean fields
    public static void assertBooleanField(Response response, String fieldName, boolean expected){
        Assert.assertEquals(response.jsonPath().getBoolean(fieldName), expected);
    }

    //Assert response field - with JSON serialization
    public static <T> void assertResponseFieldEqualsSerialization(Response response, String fieldName, T expectedObject){
        Object actualValue = response.jsonPath().get(fieldName);
        String actualJson = JsonHelper.toJson(actualValue);
        String expectedJson = JsonHelper.toJson(expectedObject);

        Assert.assertEquals(actualJson, expectedJson,
                "Field: " + fieldName + " comparison mismatch.");
    }

    //Assert complete response against expected object
    public static <T> void assertResponseMatchesObject(Response response, Class<T> clazz, T expectedObject){
        T actualObject = JsonHelper.fromJson(response.getBody().asString(), clazz);
        Assert.assertEquals(actualObject, expectedObject,
                "Response object mismatch.");
    }

    //Assert complete response against list responses
    public static <T> void assertResponseMatchesList(Response response, Class<T> elementType, List<T> expectedList){
        List<T> actualList = JsonHelper.fromJsonList(response.getBody().asString(), elementType);
        Assert.assertEquals(actualList, expectedList,
                "Response list mismatch");
    }

    //Enhanced Schema Validation
    public static void assertResponseStructure(Response response, Class<?> expectedClass){
        try{
            JsonHelper.fromJson(response.getBody().asString(), expectedClass);
        } catch(JsonParseException e){
            Assert.fail("Response structure doesn't match expected class: " + e.getMessage());
        }
    }

    //Asserts that a string is a valid ISO date (yyyy-mm-dd) format
    public static void assertValidIsoDate(String dateString){
        Assert.assertTrue(RegexHelper.isValidDate(dateString),
                "The date is not a valid format");
    }

    //Soft Assertions
    //Assert that the status code is a specific value
    public static void softAssertStatusCode(Response response, int expectedStatusCode){
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code: " + expectedStatusCode + " but got: " + response.getStatusCode());
    }

    //Assert multiple JSON fields
    public static void softAssertJsonFields(Response response, Map<String, Object> fieldValueMap){
        SoftAssert softAssert = new SoftAssert();
        fieldValueMap.forEach((field, expectedValue) -> {
            Object actualValue = response.jsonPath().get(field);
            softAssert.assertEquals(actualValue, expectedValue,
            String.format("Field '%s' mismatch. Expected: '%s', Actual: '%s'",
                    field, expectedValue, actualValue));
        });
        softAssert.assertAll();
    }

    //Assert status code and error message
    public static void softAssertErrorResponse(Response response, int expectedStatus, String expectedErrorMsg){
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), expectedStatus, "Status code mismatch");
        softAssert.assertEquals(response.jsonPath().getString("error"), expectedErrorMsg,
                "Error message mismatch.");
        softAssert.assertAll();
    }

    //Validate JSON schema without failing the test immediately
    public static void softAssertJsonSchema(Response response, String schemaPath){
        SoftAssert softAssert = new SoftAssert();
        try{
            response.then().assertThat().body(matchesJsonSchema(
                    new File(ApiAssertions.class.getClassLoader().getResource(schemaPath).getFile())
            ));
        } catch (AssertionError e){
            softAssert.fail("JSON schema validation failed: " + e.getMessage());
        }
        softAssert.assertAll();
    }

    //Validate multiple response headers
    public static void softAssertHeaders(Response response, Map<String, String> headerValueMap){
        SoftAssert softAssert = new SoftAssert();
        headerValueMap.forEach((header, expectedValue) -> {
            String actualValue = response.getHeader(header);
            softAssert.assertEquals(actualValue, expectedValue,
                    String.format("Header '%s' mismatch. Expected: '%s', Actual: '%s'",
                            header, expectedValue, actualValue));
        });
        softAssert.assertAll();
    }

    //Verify response time is within acceptable limits
    public static void softAssertResponseTime(Response response, long maxTimeMs){
        SoftAssert softAssert = new SoftAssert();
        long actualTime = response.getTime();
        softAssert.assertTrue(actualTime < maxTimeMs,
                String.format("Response time exceeded maximum threshold. Expected: '%s', Actual: '%s'",
                        maxTimeMs, actualTime));
        softAssert.assertAll();
    }

    //Verify array size and specific elements
    public static void softAssertArray(Response response, String arrayField, int expectedSize,
                                       Map<Integer, Object> indexValueMap){
        SoftAssert softAssert = new SoftAssert();
        List<Object> array = response.jsonPath().getList(arrayField);
        softAssert.assertEquals(array.size(), expectedSize, "Array size mismatch");
        indexValueMap.forEach((index, expectedValue) -> {
            softAssert.assertEquals(array.get(index), expectedValue,
                    String.format("Array index %d mismatch. Expected: '%s', Actual: '%s'",
                            index, expectedValue, array.get(index)));
        });
        softAssert.assertAll();
    }

    //Validate status code, JSON fields, headers and schema all in one call
    public static void softAssertAll(Response response, int expectedStatusCode,
                                     Map<String, Object> fieldValueMap,
                                     Map<String, String> headerValueMap,
                                     String schemaPath){
        SoftAssert softAssert = new SoftAssert();

        //Status code
        softAssert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Expected status code: " + expectedStatusCode + " but got: " + response.getStatusCode());

        //JSON fields
        fieldValueMap.forEach((field, expectedValue) -> {
            softAssert.assertEquals(response.jsonPath().get(field), expectedValue,
                    "Field '" + field + "' mismatch");
        });

        //Headers
        headerValueMap.forEach((header, expectedValue) -> {
            softAssert.assertEquals(response.getHeader(header), expectedValue,
                    "Header '" + header + "' mismatch");
        });

        //Schema (optional)
        if (schemaPath != null){
            try {
                response.then().assertThat().body(matchesJsonSchema(
                        new File(ApiAssertions.class.getClassLoader().getResource(schemaPath).getFile())
                ));
            } catch (AssertionError e){
                softAssert.fail("Schema validation failed: " + e.getMessage());
            }
        }

        softAssert.assertAll();
    }

    //Soft assert response field - with JSON serialization
    public static <T> void softAssertResponseFieldEquals(Response response, String fieldName, T expectedObject){
        SoftAssert softAssert = new SoftAssert();
        Object actualValue = response.jsonPath().get(fieldName);
        String actualJson = JsonHelper.toJson(actualValue);
        String expectedJson = JsonHelper.toJson(expectedObject);

        softAssert.assertEquals(actualJson, expectedJson,
                "Field: " + fieldName + " comparison mismatch.");
        softAssert.assertAll();
    }

}