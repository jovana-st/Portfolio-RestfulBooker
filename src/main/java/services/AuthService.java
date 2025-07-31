package services;

import config.Constants;
import core.RequestSpecifications;
import io.restassured.specification.RequestSpecification;
import models.AuthRequest;

import static io.restassured.RestAssured.given;

public class AuthService {

    public static String getAuthToken(){
        AuthRequest authBody = new AuthRequest("admin", "password123");

        return given().spec(RequestSpecifications.baseSpecAuth).body(authBody)
                .post(Constants.AUTH_ENDPOINT)
                .then().extract().path("token");

    }
}