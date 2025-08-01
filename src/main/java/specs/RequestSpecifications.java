package specs;

import config.Constants;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import services.AuthService;

public class RequestSpecifications {

    public static RequestSpecification baseSpecAuth = new RequestSpecBuilder().setBaseUri(Constants.BASE_URL)
            .addHeader("Content-Type", "application/json").build();

    public static RequestSpecification baseSpecGetBooking = new RequestSpecBuilder().setBaseUri(Constants.BASE_URL)
            .build();

    public static RequestSpecification baseSpecCreateBooking = new RequestSpecBuilder().setBaseUri(Constants.BASE_URL)
            .addHeader("Content-Type", "application/json").build();

    public static RequestSpecification baseSpecUpdateBooking(String username, String password){
        return new RequestSpecBuilder().setBaseUri(Constants.BASE_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Acccept", "application/json")
                .addHeader("Cookie", "token=" + AuthService.getAuthToken(username, password))
                .build();
    }

    public static RequestSpecification baseSpecDeleteBooking(String username, String password){
        return new RequestSpecBuilder().setBaseUri(Constants.BASE_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", "token=" + AuthService.getAuthToken(username, password))
                .build();
    }
}