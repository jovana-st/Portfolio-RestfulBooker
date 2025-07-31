package tests;

import config.Constants;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.ApiAssertions;

import static io.restassured.RestAssured.given;

public class PingTest {

    @Test(priority = 1)
    public void healthCheck(){
        Response response = given().baseUri(Constants.BASE_URL)
                .when().get(Constants.PING_ENDPOINT)
                .then().extract().response();

        ApiAssertions.assertStatusCode(response, 201);
    }

}