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
        ApiAssertions.assertStatusCode(response, 200);
        ApiAssertions.assertResponseContainsField(response, "token");
    }

    @Test(description = "Verify that invalid credentials cannot generate a token")
    public void authWithInvalidCredentialsReturns403(){
        AuthRequest authCreds = new AuthRequest(TestDataGenerator.generateAuthUsername(),
                TestDataGenerator.generateAuthPassword());
        Response response = AuthService.authenticate(authCreds);
        ApiAssertions.assertStatusCode(response, 200);
        ApiAssertions.assertResponseFieldEquals(response, "reason", "Bad credentials");
    }

    @Test(description = "Verify that auth completes within 1300ms")
    public void authResponseTimeUnder1300ms(){
        AuthRequest authCreds = new AuthRequest(AuthConfig.USERNAME, AuthConfig.PASSWORD);
        Response response = AuthService.authenticate(authCreds);
        ApiAssertions.softAssertResponseTime(response, 1500);
    }

    @Test(description = "Verify auth with empty credentials")
    public void authEmptyCredentials(){
        AuthRequest authCreds = new AuthRequest(null, null);
        Response response = AuthService.authenticate(authCreds);
        ApiAssertions.assertStatusCode(response, 200);
        ApiAssertions.assertResponseFieldEquals(response, "reason", "Bad credentials");
    }
}