package tests;

import config.Constants;
import config.Endpoints;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utils.ApiAssertions;

import static io.restassured.RestAssured.given;

public class PingTest {

    @Test(priority = 1)
    public void healthCheck(){
        Response response = given().baseUri(Constants.BASE_URL)
                .when().get(Endpoints.PING)
                .then().extract().response();

        ApiAssertions.softAssertStatusCode(response, 201);
    }

}