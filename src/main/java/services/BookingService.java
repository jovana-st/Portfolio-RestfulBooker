package services;

import config.Endpoints;
import specs.RequestSpecifications;
import io.restassured.response.Response;
import models.BookingRequest;

import java.time.LocalDate;
import java.util.Map;

import static specs.RequestSpecifications.*;
import static io.restassured.RestAssured.given;

public class BookingService {

    public static Response getBookingIds(){
        return given().spec(baseSpecGetBooking)
                .when().get(Endpoints.BOOKING)
                .then().log().all().extract().response();
    }

    public static Response getBookingsByName(String firstname, String lastname){
        return given().spec(baseSpecGetBooking)
                .queryParam("firstname", firstname)
                .queryParam("lastname", lastname)
                .when().get(Endpoints.BOOKING)
                .then().log().all().extract().response();
    }

    public static Response getBookingsByCheckinAndCheckout(LocalDate checkin, LocalDate checkout){
        return given().spec(baseSpecGetBooking)
                .queryParam("checkin", checkin.toString())
                .queryParam("checkout", checkout.toString())
                .when().get(Endpoints.BOOKING)
                .then().log().all().extract().response();
    }

    public static Response getBooking(int id){
        return given().spec(baseSpecGetBooking)
                .pathParam("id", id)
                .when().get(Endpoints.BOOKING_ID)
                .then().log().all().extract().response();
    }

    public static Response createBooking(BookingRequest bookingBody){
        return given().spec(baseSpecCreateBooking)
                .body(bookingBody)
                .when().post(Endpoints.BOOKING)
                .then().log().all().extract().response();
    }

    public static Response updateBooking(String username, String password, int id, BookingRequest bookingBody){
        return given().spec(RequestSpecifications.baseSpecUpdateBooking(username, password))
                .pathParam("id", id)
                .body(bookingBody)
                .when().put(Endpoints.BOOKING_ID)
                .then().log().all().extract().response();
    }

    public static Response partialUpdateBooking(String username, String password, int id, Map<String, Object> updates){
        return given().spec(RequestSpecifications.baseSpecUpdateBooking(username, password))
                .pathParam("id", id)
                .body(updates)
                .when().patch(Endpoints.BOOKING_ID)
                .then().log().all().extract().response();
    }

    public static Response deleteBooking(String username, String password, int id){
        return given().spec(RequestSpecifications.baseSpecDeleteBooking(username, password))
                .pathParam("id", id)
                .when().delete(Endpoints.BOOKING_ID)
                .then().log().all().extract().response();
    }



}
