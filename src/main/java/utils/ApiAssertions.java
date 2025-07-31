package utils;

import io.restassured.response.Response;
import org.testng.Assert;
import java.io.File;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class ApiAssertions {

    //Assert that the status code is a specific value
    public static void assertStatusCode(Response response, int expectedStatusCode){
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode);
    }

    //Assert that the response contains a specific field
    public static void assertResponseContainsField(Response response, String fieldName){
        Assert.assertNotNull(response.jsonPath().get(fieldName));
    }

    //Assert that a field in the response equals a specific value
    public static void assertResponseFieldEquals(Response response, String fieldName, String expectedValue){
        Assert.assertEquals(response.jsonPath().get(fieldName), expectedValue);
    }

    //Assert that the response time is less than a specific amount
    public static void assertResponseTimeLessThan(Response response, long maxTime){
        Assert.assertTrue(response.getTime() < maxTime);
    }

    //Assert that the response body is empty
    public static void assertEmptyBody(Response response){
        Assert.assertTrue(response.getBody().asString().isEmpty());
    }

    //Assert that the header exists
    public static void assertHeaderExists(Response response, String headerName){
        Assert.assertTrue(response.getHeaders().hasHeaderWithName(headerName));
    }

    //Assert JSON Schema
    public static void assertJsonSchema(Response response, String schemaPath){
        response.then().assertThat().body(matchesJsonSchema(new File(schemaPath)));
    }

}
