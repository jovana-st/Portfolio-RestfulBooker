package services;

import config.Endpoints;
import io.restassured.response.Response;
import specs.RequestSpecifications;
import models.AuthRequest;
import java.time.Duration;
import static io.restassured.RestAssured.given;

public class AuthService {

    //Contains token caching & refresh logic
    private static final Duration TOKEN_TTL = Duration.ofHours(1);
    private static String cachedToken;
    private static long tokenExpiryTime;

    public static String getAuthToken(String username, String password){

        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime){
            return cachedToken;
        }

        AuthRequest authBody = new AuthRequest(username, password);

        try {
            cachedToken = given().spec(RequestSpecifications.baseSpecAuth).body(authBody)
                    .post(Endpoints.AUTH)
                    .then().extract().path("token");

            tokenExpiryTime = System.currentTimeMillis() + TOKEN_TTL.toMillis();
            return cachedToken;
        } catch (Exception e){
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

    public static Response authenticate(AuthRequest authRequest){
        return given().spec(RequestSpecifications.baseSpecAuth)
                .body(authRequest)
                .post(Endpoints.AUTH)
                .then().extract().response();
    }
}