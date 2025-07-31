package services;

import config.Constants;
import specs.RequestSpecifications;
import io.restassured.response.Response;
import models.BookingRequest;

import java.util.Map;

import static specs.RequestSpecifications.*;
import static io.restassured.RestAssured.given;

public class BookingService {

    public static Response getBookingIds(){
        return given().spec(baseSpecGetBooking)
                .when().get(Constants.BOOKING_ENDPOINT)
                .then().extract().response();
    }

    public static Response getBooking(int id){
        return given().spec(baseSpecGetBooking)
                .when().get(Constants.BOOKING_ENDPOINT + "/" + id)
                .then().extract().response();
    }

    public static Response createBooking(BookingRequest bookingBody){
        return given().spec(baseSpecCreateBooking)
                .body(bookingBody)
                .when().post(Constants.BOOKING_ENDPOINT)
                .then().extract().response();
    }

    public static Response updateBooking(int id, BookingRequest bookingBody){
        return given().spec(RequestSpecifications.baseSpecUpdateBooking())
                .body(bookingBody)
                .when().put(Constants.BOOKING_ENDPOINT + "/" + id)
                .then().extract().response();
    }

    public static Response partialUpdateBooking(int id, Map<String, Object> updates){
        return given().spec(RequestSpecifications.baseSpecUpdateBooking())
                .body(updates)
                .when().patch(Constants.BOOKING_ENDPOINT + "/" + id)
                .then().extract().response();
    }

    public static Response deleteBooking(int id){
        return given().spec(RequestSpecifications.baseSpecDeleteBooking())
                .when().delete(Constants.BOOKING_ENDPOINT + "/" + id)
                .then().extract().response();
    }


}
