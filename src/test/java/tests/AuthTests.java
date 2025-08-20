package tests;

import config.AuthConfig;
import io.restassured.response.Response;
import models.AuthRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import services.AuthService;
import utils.ApiAssertions;
import utils.TestDataGenerator;

public class AuthTests {

    @Test(description = "Verify successful token generation with valid credentials")
    public void authWithValidCredentialsReturnsToken(){
        AuthRequest authCreds = new AuthRequest(AuthConfig.USERNAME, AuthConfig.PASSWORD);
        Response response = AuthService.authenticate(authCreds);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 200);
        //Verify that the response time is less than 1500
        ApiAssertions.softAssertResponseTime(response, 1500);
        //Verify that the response contains the token
        ApiAssertions.assertResponseContainsField(response, "token");
    }

    @Test(description = "Verify that invalid credentials cannot generate a token")
    public void authWithInvalidCredentialsReturns403(){
        AuthRequest authCreds = new AuthRequest(TestDataGenerator.generateAuthUsername(),
                TestDataGenerator.generateAuthPassword());
        Response response = AuthService.authenticate(authCreds);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 200);
        //Verify that the error message is correct and clear
        ApiAssertions.assertResponseFieldEquals(response, "reason", "Bad credentials");
    }

    @Test(description = "Verify auth with empty credentials")
    public void authEmptyCredentials(){
        AuthRequest authCreds = new AuthRequest(null, null);
        Response response = AuthService.authenticate(authCreds);
        //Verify that status code is correct
        ApiAssertions.assertStatusCode(response, 200);
        //Verify that the error message is correct and clear
        ApiAssertions.assertResponseFieldEquals(response, "reason", "Bad credentials");
    }
}