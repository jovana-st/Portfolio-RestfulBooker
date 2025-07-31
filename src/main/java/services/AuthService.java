package services;

import config.Constants;
import specs.RequestSpecifications;
import models.AuthRequest;

import static io.restassured.RestAssured.given;

public class AuthService {

    //Contains token caching & refresh logic
    private static String cachedToken;
    private static long tokenExpiryTime;

    public static String getAuthToken(){

        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime){
            return cachedToken;
        }

        AuthRequest authBody = new AuthRequest("admin", "password123");

        try {
            cachedToken = given().spec(RequestSpecifications.baseSpecAuth).body(authBody)
                    .post(Constants.AUTH_ENDPOINT)
                    .then().extract().path("token");

            tokenExpiryTime = System.currentTimeMillis() + 3600_000;
            return cachedToken;
        } catch (Exception e){
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }

    }
}
